const { app, BrowserWindow, Menu } = require('electron')
const path = require('path')

// 你的服务器地址
const SERVER_URL = 'http://8.138.84.36/'

function createWindow() {
  const win = new BrowserWindow({
    width: 1400,
    height: 900,
    minWidth: 1024,
    minHeight: 768,
    icon: path.join(__dirname, '../public/favicon.ico'),
    webPreferences: {
      nodeIntegration: false,
      contextIsolation: true
    },
    // 窗口标题
    title: '宠物健康管理系统'
  })

  // 加载服务器地址
  win.loadURL(SERVER_URL)

  // 设置简单菜单
  const template = [
    {
      label: '文件',
      submenu: [
        { 
          label: '刷新', 
          accelerator: 'CmdOrCtrl+R',
          click: () => win.reload() 
        },
        { 
          label: '开发者工具', 
          accelerator: 'F12',
          click: () => win.webContents.toggleDevTools() 
        },
        { type: 'separator' },
        { 
          label: '退出', 
          accelerator: 'CmdOrCtrl+Q',
          click: () => app.quit() 
        }
      ]
    }
  ]
  
  Menu.setApplicationMenu(Menu.buildFromTemplate(template))

  // 处理窗口标题
  win.on('page-title-updated', (e) => {
    e.preventDefault()
  })
}

// 应用准备就绪时创建窗口
app.whenReady().then(() => {
  createWindow()

  // macOS 点击 dock 图标时重新创建窗口
  app.on('activate', () => {
    if (BrowserWindow.getAllWindows().length === 0) {
      createWindow()
    }
  })
})

// 所有窗口关闭时退出应用（macOS 除外）
app.on('window-all-closed', () => {
  if (process.platform !== 'darwin') {
    app.quit()
  }
})
