<template>
  <div class="dashboard-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-info">
        <h1>📊 数据看板</h1>
        <p>系统运营数据概览</p>
      </div>
      <el-button type="primary" :icon="Refresh" @click="loadAllData" :loading="loading">
        刷新数据
      </el-button>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-cards" v-loading="loading">
      <div class="stat-card users">
        <div class="stat-icon">👥</div>
        <div class="stat-content">
          <div class="stat-value">{{ stats.userCount || 0 }}</div>
          <div class="stat-label">用户总数</div>
        </div>
      </div>
      <div class="stat-card pets">
        <div class="stat-icon">🐾</div>
        <div class="stat-content">
          <div class="stat-value">{{ stats.petCount || 0 }}</div>
          <div class="stat-label">宠物总数</div>
        </div>
      </div>
      <div class="stat-card providers">
        <div class="stat-icon">🏥</div>
        <div class="stat-content">
          <div class="stat-value">{{ stats.providerCount || 0 }}</div>
          <div class="stat-label">服务商总数</div>
        </div>
      </div>
      <div class="stat-card appointments">
        <div class="stat-icon">📅</div>
        <div class="stat-content">
          <div class="stat-value">{{ stats.appointmentCount || 0 }}</div>
          <div class="stat-label">预约总数</div>
        </div>
      </div>
    </div>

    <!-- 图表区域 -->
    <div class="charts-section">
      <div class="chart-row">
        <!-- 预约趋势图 -->
        <div class="chart-card large">
          <div class="chart-header">
            <h3>📈 近7天预约趋势</h3>
          </div>
          <div ref="trendChartRef" class="chart-container"></div>
        </div>
      </div>

      <div class="chart-row">
        <!-- 宠物类型分布 -->
        <div class="chart-card">
          <div class="chart-header">
            <h3>🐕 宠物类型分布</h3>
          </div>
          <div ref="petTypeChartRef" class="chart-container"></div>
        </div>

        <!-- 用户角色分布 -->
        <div class="chart-card">
          <div class="chart-header">
            <h3>👤 用户角色分布</h3>
          </div>
          <div ref="roleChartRef" class="chart-container"></div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { Refresh } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import { getStats, getPetTypeDistribution, getAppointmentTrend, getUserRoleDistribution } from '@/api/dashboard'

const loading = ref(false)
const stats = ref({})

// 图表实例引用
const trendChartRef = ref(null)
const petTypeChartRef = ref(null)
const roleChartRef = ref(null)

let trendChart = null
let petTypeChart = null
let roleChart = null

// 加载统计数据
const loadStats = async () => {
  try {
    const res = await getStats()
    stats.value = res.data || {}
  } catch (e) {
    console.error('加载统计数据失败:', e)
  }
}

// 初始化预约趋势图
const initTrendChart = async () => {
  try {
    const res = await getAppointmentTrend()
    const data = res.data || []
    
    if (!trendChartRef.value) return
    
    trendChart = echarts.init(trendChartRef.value)
    trendChart.setOption({
      tooltip: {
        trigger: 'axis',
        axisPointer: { type: 'shadow' }
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
      },
      xAxis: {
        type: 'category',
        data: data.map(item => item.date),
        axisLabel: { color: '#666' }
      },
      yAxis: {
        type: 'value',
        minInterval: 1,
        axisLabel: { color: '#666' }
      },
      series: [{
        name: '预约数',
        type: 'bar',
        data: data.map(item => item.count),
        itemStyle: {
          borderRadius: [4, 4, 0, 0],
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#667eea' },
            { offset: 1, color: '#764ba2' }
          ])
        },
        emphasis: {
          itemStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: '#764ba2' },
              { offset: 1, color: '#667eea' }
            ])
          }
        }
      }]
    })
  } catch (e) {
    console.error('加载预约趋势失败:', e)
  }
}

// 初始化宠物类型饼图
const initPetTypeChart = async () => {
  try {
    const res = await getPetTypeDistribution()
    const data = res.data || []
    
    if (!petTypeChartRef.value) return
    
    petTypeChart = echarts.init(petTypeChartRef.value)
    petTypeChart.setOption({
      tooltip: {
        trigger: 'item',
        formatter: '{b}: {c} ({d}%)'
      },
      legend: {
        orient: 'horizontal',
        bottom: '5%',
        textStyle: { color: '#666' }
      },
      series: [{
        type: 'pie',
        radius: ['40%', '70%'],
        center: ['50%', '45%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 8,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: false,
          position: 'center'
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 18,
            fontWeight: 'bold'
          }
        },
        labelLine: { show: false },
        data: data,
        color: ['#667eea', '#36cfc9', '#ff7875', '#ffc53d', '#95de64', '#b37feb']
      }]
    })
  } catch (e) {
    console.error('加载宠物类型分布失败:', e)
  }
}

// 初始化用户角色饼图
const initRoleChart = async () => {
  try {
    const res = await getUserRoleDistribution()
    const data = res.data || []
    
    if (!roleChartRef.value) return
    
    roleChart = echarts.init(roleChartRef.value)
    roleChart.setOption({
      tooltip: {
        trigger: 'item',
        formatter: '{b}: {c} ({d}%)'
      },
      legend: {
        orient: 'horizontal',
        bottom: '5%',
        textStyle: { color: '#666' }
      },
      series: [{
        type: 'pie',
        radius: ['40%', '70%'],
        center: ['50%', '45%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 8,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: false,
          position: 'center'
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 18,
            fontWeight: 'bold'
          }
        },
        labelLine: { show: false },
        data: data,
        color: ['#1890ff', '#52c41a', '#fa541c']
      }]
    })
  } catch (e) {
    console.error('加载用户角色分布失败:', e)
  }
}

// 加载所有数据
const loadAllData = async () => {
  loading.value = true
  try {
    await loadStats()
    await nextTick()
    await Promise.all([
      initTrendChart(),
      initPetTypeChart(),
      initRoleChart()
    ])
  } finally {
    loading.value = false
  }
}

// 响应窗口大小变化
const handleResize = () => {
  trendChart?.resize()
  petTypeChart?.resize()
  roleChart?.resize()
}

onMounted(() => {
  loadAllData()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  trendChart?.dispose()
  petTypeChart?.dispose()
  roleChart?.dispose()
})
</script>

<style lang="scss" scoped>
.dashboard-page {
  max-width: 1400px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;

  h1 {
    font-size: 24px;
    font-weight: 600;
    color: #1f2937;
    margin: 0 0 4px 0;
  }

  p {
    font-size: 14px;
    color: #6b7280;
    margin: 0;
  }
}

// 统计卡片
.stats-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 24px;
}

.stat-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  transition: transform 0.3s, box-shadow 0.3s;

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 12px 32px rgba(0, 0, 0, 0.1);
  }

  .stat-icon {
    font-size: 40px;
    width: 64px;
    height: 64px;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 12px;
  }

  &.users .stat-icon { background: linear-gradient(135deg, #667eea20, #764ba220); }
  &.pets .stat-icon { background: linear-gradient(135deg, #36cfc920, #1890ff20); }
  &.providers .stat-icon { background: linear-gradient(135deg, #52c41a20, #95de6420); }
  &.appointments .stat-icon { background: linear-gradient(135deg, #ff787520, #ffc53d20); }

  .stat-content {
    .stat-value {
      font-size: 32px;
      font-weight: 700;
      color: #1f2937;
      line-height: 1.2;
    }

    .stat-label {
      font-size: 14px;
      color: #6b7280;
      margin-top: 4px;
    }
  }
}

// 图表区域
.charts-section {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.chart-row {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;

  &:first-child {
    grid-template-columns: 1fr;
  }
}

.chart-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);

  &.large {
    .chart-container {
      height: 300px;
    }
  }

  .chart-header {
    margin-bottom: 16px;

    h3 {
      font-size: 16px;
      font-weight: 600;
      color: #1f2937;
      margin: 0;
    }
  }

  .chart-container {
    height: 280px;
  }
}

// 响应式
@media (max-width: 1200px) {
  .stats-cards {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .stats-cards {
    grid-template-columns: 1fr;
  }

  .chart-row {
    grid-template-columns: 1fr;
  }
}
</style>
