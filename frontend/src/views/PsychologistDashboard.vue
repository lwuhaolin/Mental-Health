<template>
  <div class="app-page app-page--wide dashboard-container">
    <div class="app-page__inner">
    <!-- 专家信息头部 -->
    <div class="header-section">
      <div class="expert-header">
        <div class="expert-avatar">
          <el-avatar :size="80" :src="resolveAvatarUrl(psychologistInfo.avatar) || defaultAvatar" />
        </div>
        <div class="expert-actions" style="margin-top:8px;">
          <el-upload class="avatar-uploader" :show-file-list="false" :http-request="doAvatarUpload" :before-upload="beforeAvatarUpload">
            <el-button size="small" type="primary" :loading="avatarUploading">更换头像</el-button>
          </el-upload>
        </div>
        <div class="expert-info">
          <h2>{{ psychologistInfo.name }}</h2>
          <div class="expert-title">{{ psychologistInfo.title }}</div>
          <div class="expert-qualifications">
            <span v-for="(qualification, index) in psychologistInfo.qualifications" :key="index" class="qualification">
              {{ qualification }}
            </span>
          </div>
          <div class="expert-details">
            <div class="detail-item">
              <i class="el-icon-time"></i>
              <span>{{ psychologistInfo.experience }}</span>
            </div>
            <div class="detail-item">
              <i class="el-icon-medal"></i>
              <span>{{ psychologistInfo.certification }}</span>
            </div>
            <div class="detail-item">
              <i class="el-icon-school"></i>
              <span>{{ psychologistInfo.education }}</span>
            </div>
            <div class="detail-item">
              <i class="el-icon-money"></i>
              <span>¥{{ psychologistInfo.hourlyRate }}/小时</span>
            </div>
          </div>
          <div class="expert-specialization">
            <strong>擅长领域：</strong>{{ psychologistInfo.specialization }}
          </div>
          <div class="expert-languages">
            <strong>服务语言：</strong>
            <span v-for="(language, index) in psychologistInfo.languages" :key="index" class="language-tag">
              {{ language }}
            </span>
          </div>
          <div class="expert-availability">
            <strong>可预约时间：</strong>{{ psychologistInfo.availableTime }}
          </div>
        </div>
      </div>
    </div>

    <!-- 页面顶部导航 -->
    <div class="page-header">
      <div class="header-left">
        <el-button type="primary" @click="handleBackToLogin" size="small" class="logout-button">
          <i class="el-icon-switch-button"></i>
          退出登录
        </el-button>
      </div>
      <div class="header-center">
        <h2>心理专家工作台</h2>
        <div class="welcome-text">欢迎回来，{{ currentPsychologistName }}</div>
      </div>
      <div class="header-right">
        <!-- 功能标签切换 -->
        <el-button-group class="function-tabs">
          <el-button :type="activeTab === 'main' ? 'primary' : ''" @click="activeTab = 'main'" size="small">
            <i class="el-icon-data-line"></i> 总览
          </el-button>
          <el-button :type="activeTab === 'calendar' ? 'primary' : ''" @click="activeTab = 'calendar'" size="small">
            <i class="el-icon-calendar"></i> 日程管理
          </el-button>
          <el-button :type="activeTab === 'statistics' ? 'primary' : ''" @click="activeTab = 'statistics'" size="small">
            <i class="el-icon-data-analysis"></i> 数据统计
          </el-button>
          <el-button :type="activeTab === 'notes' ? 'primary' : ''" @click="activeTab = 'notes'" size="small">
            <i class="el-icon-document"></i> 咨询笔记
          </el-button>
        </el-button-group>
      </div>
    </div>

    <!-- 顶部统计卡片 -->
    <div class="stats-section">
      <div class="stats-grid">
        <div class="stat-card">
          <div class="stat-content">
            <div class="stat-icon patient">
              <TeamOutlined />
            </div>
            <div class="stat-text">
              <div class="stat-value">{{ stats.patientCount }}</div>
              <div class="stat-label">服务患者</div>
            </div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-content">
            <div class="stat-icon consultation">
              <MessageOutlined />
            </div>
            <div class="stat-text">
              <div class="stat-value">{{ stats.consultationCount }}</div>
              <div class="stat-label">累计咨询数</div>
            </div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-content">
            <div class="stat-icon rating">
              <StarOutlined />
            </div>
            <div class="stat-text">
              <div class="stat-value">{{ stats.avgRating }}</div>
              <div class="stat-label">平均评分</div>
            </div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-content">
            <div class="stat-icon income">
              <WalletOutlined />
            </div>
            <div class="stat-text">
              <div class="stat-value">¥{{ stats.totalIncome }}</div>
              <div class="stat-label">总收入</div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 主内容区域 - 总览 -->
    <div class="main-content" v-show="activeTab === 'main'">
      <el-row :gutter="20">
        <!-- 患者列表 -->
        <el-col :span="12">
          <div class="panel">
            <div class="panel-header">
              <h3>我的患者列表</h3>
              <el-button type="primary" size="small">刷新列表</el-button>
            </div>
            <div class="panel-content">
              <el-table :data="patientList" style="width: 100%">
                <el-table-column prop="name" label="患者姓名" width="100" />
                <el-table-column prop="age" label="年龄" width="80" />
                <el-table-column prop="lastConsultation" label="最近咨询时间" width="160">
                  <template #default="scope">
                    {{ formatDateTime(scope.row.lastConsultation) }}
                  </template>
                </el-table-column>
                <el-table-column prop="status" label="状态" width="100">
                  <template #default="scope">
                    <el-tag :type="getStatusTagType(scope.row.status)">
                      {{ scope.row.status }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column label="操作" width="220">
                  <template #default="scope">
                    <el-button size="small" @click="viewPatientDetail(scope.row)">查看详情</el-button>
                    <el-button 
                      size="small" 
                      type="success" 
                      @click="updatePatientStatus(scope.row, '已完成')"
                      :disabled="!canCompleteConsultation(scope.row)">
                      {{ getCompleteButtonText(scope.row) }}
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </div>
        </el-col>

        <!-- 预约管理 -->
        <el-col :span="12">
          <div class="panel">
            <div class="panel-header">
              <h3>咨询预约列表</h3>
              <el-button type="primary" size="small" @click="loadAppointmentList">刷新</el-button>
            </div>
            <div class="panel-content">
              <el-table :data="appointmentList" style="width: 100%" v-loading="appointmentLoading">
                <el-table-column prop="topic" label="咨询主题" width="120" />
                <el-table-column prop="preferredTime" label="预约时间" width="140">
                  <template #default="scope">
                    {{ formatTime(scope.row.preferredTime) }}
                  </template>
                </el-table-column>
                <el-table-column prop="contactInfo" label="联系方式" width="120" />
                <el-table-column prop="status" label="状态" width="100">
                  <template #default="scope">
                    <el-tag :type="getAppointmentStatusTagType(scope.row.status)">
                      {{ formatAppointmentStatus(scope.row.status) || '待确认' }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column label="操作" width="180">
                  <template #default="scope">
                    <el-button size="small" type="primary" @click="handleAcceptAppointment(scope.row)" 
                              :disabled="scope.row.status === 'confirmed' || scope.row.status === 'completed' || scope.row.status === 'cancelled'">
                      接单
                    </el-button>
                    <el-button size="small" type="danger" @click="handleCancelAppointment(scope.row)"
                              :disabled="scope.row.status === 'confirmed' || scope.row.status === 'completed' || scope.row.status === 'cancelled'">
                      拒绝
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </div>
        </el-col>
      </el-row>

      <!-- 进行中的咨询列表 -->
      <el-row :gutter="20" style="margin-top: 20px;">
        <el-col :span="24">
          <div class="panel">
            <div class="panel-header">
              <h3>当前咨询会话列表</h3>
              <el-button type="primary" size="small" @click="refreshConsultations">刷新</el-button>
            </div>
            <div class="panel-content">
              <el-table :data="activeConsultations" style="width: 100%" v-loading="consultationsLoading">
                <el-table-column prop="patientName" label="患者姓名" width="120" />
                <el-table-column prop="title" label="咨询主题" width="180" />
                <el-table-column prop="status" label="状态" width="100">
                  <template #default="scope">
                    <el-tag :type="getStatusTagType(scope.row.status)">
                      {{ scope.row.status }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column prop="userFeedback" label="用户反馈" min-width="200">
                  <template #default="scope">
                    <span v-if="scope.row.userFeedback">{{ scope.row.userFeedback }}</span>
                    <span v-else style="color: #999;">暂无反馈</span>
                  </template>
                </el-table-column>
                <el-table-column prop="userRating" label="用户评分" width="120">
                  <template #default="scope">
                    <el-rate v-if="scope.row.userRating" v-model="scope.row.userRating" disabled size="small"></el-rate>
                    <span v-else style="color: #999;">未评分</span>
                  </template>
                </el-table-column>
                <el-table-column prop="createTime" label="创建时间" width="160">
                  <template #default="scope">
                    {{ formatDateTime(scope.row.createTime) }}
                  </template>
                </el-table-column>
                <el-table-column label="操作" width="120">
                  <template #default="scope">
                    <el-button size="small" type="primary" @click="viewConsultationFeedback(scope.row)">
                      查看反馈
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </div>
        </el-col>
      </el-row>

      <!-- 在线咨询聊天弹窗 -->
      <el-dialog
        v-model="showChatDialog"
        :title="`在线咨询 - ${currentConsultation?.patientName || '未知患者'}`"
        width="900px"
        :before-close="handleCloseChat"
        class="chat-dialog"
      >
        <div class="chat-container">
          <!-- 聊天消息列表 -->
          <div class="chat-messages" ref="chatMessages">
            <div v-if="chatMessages.length === 0" class="empty-chat">
              <i class="el-icon-chat-line-round"></i>
              <p>暂无聊天记录，开始本次咨询会话吧</p>
            </div>
            <div v-else v-for="message in chatMessages" :key="message.id" 
                 :class="['message-item', message.senderType === 'psychologist' ? 'message-right' : 'message-left']">
              <div class="message-avatar">
                <el-avatar :size="40" :src="getAvatar(message.senderType)" />
              </div>
              <div class="message-content">
                <div class="message-header">
                  <span class="sender-name">{{ message.senderType === 'psychologist' ? '我' : currentConsultation?.patientName || '未知患者' }}</span>
                </div>
                <div class="message-bubble">
                  <div class="message-text">{{ message.messageContent }}</div>
                </div>
                <div class="message-time">{{ formatMessageTime(message.createTime) }}</div>
              </div>
            </div>
          </div>
          
          <!-- 聊天输入区域 -->
          <div class="chat-input-area">
            <el-input
              v-model="newMessage"
              type="textarea"
              :rows="4"
              placeholder="请输入消息内容，与患者进行沟通..."
              resize="none"
              class="message-input"
              @keydown.enter.prevent="sendMessage"
            />
            <div class="chat-actions">
              <el-button type="primary" @click="sendMessage" :loading="sending" size="large">
                <i class="el-icon-s-promotion"></i>
                发送消息
              </el-button>
            </div>
          </div>
        </div>
        
        <template #footer>
          <span class="dialog-footer">
            <el-button @click="handleCloseChat" size="large">结束会话</el-button>
            <el-button type="success" @click="completeConsultation" size="large" :loading="completing">
              <i class="el-icon-check"></i>
              完成咨询
            </el-button>
          </span>
        </template>
      </el-dialog>

      <!-- 咨询详情弹窗 -->
      <el-dialog
        v-model="showDetailDialog"
        title="咨询详情"
        width="600px"
        :before-close="handleCloseDetail"
      >
        <div class="consultation-detail" v-if="currentConsultationDetail">
          <el-descriptions :column="1" border>
            <el-descriptions-item label="咨询主题">{{ currentConsultationDetail.title || '未命名' }}</el-descriptions-item>
            <el-descriptions-item label="咨询描述">{{ currentConsultationDetail.description || '暂无描述' }}</el-descriptions-item>
            <el-descriptions-item label="咨询类型">{{ currentConsultationDetail.consultationType || '-' }}</el-descriptions-item>
            <el-descriptions-item label="当前状态">
              <el-tag :type="getStatusTagType(currentConsultationDetail.status)">
                {{ currentConsultationDetail.status }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="预约时间">{{ formatTime(currentConsultationDetail.scheduledTime) }}</el-descriptions-item>
            <el-descriptions-item label="开始时间">{{ formatTime(currentConsultationDetail.startTime) }}</el-descriptions-item>
            <el-descriptions-item label="结束时间">{{ formatTime(currentConsultationDetail.endTime) }}</el-descriptions-item>
            <el-descriptions-item label="咨询时长">{{ currentConsultationDetail.duration ? currentConsultationDetail.duration + '分钟' : '未知' }}</el-descriptions-item>
            <el-descriptions-item label="咨询费用">¥{{ currentConsultationDetail.cost || 0 }}</el-descriptions-item>
            <el-descriptions-item label="用户评分">
              <el-rate
                v-model="currentConsultationDetail.userRating"
                disabled
                show-score
                text-color="#ff9900"
                score-template="{value} 分"
              />
            </el-descriptions-item>
            <el-descriptions-item label="用户反馈">{{ currentConsultationDetail.userFeedback || '暂无反馈' }}</el-descriptions-item>
          </el-descriptions>
        </div>
        
        <template #footer>
          <el-button @click="handleCloseDetail">关闭</el-button>
        </template>
      </el-dialog>
    </div>

    <!-- 日程管理 -->
    <div class="calendar-content" v-show="activeTab === 'calendar'">
      <div class="panel">
        <div class="panel-header">
          <h3>咨询日程安排</h3>
          <div class="calendar-controls">
            <el-button-group>
              <el-button size="small" @click="changeMonth(-1)">
                <i class="el-icon-arrow-left"></i> 上个月
              </el-button>
              <el-button size="small" @click="goToToday">今天</el-button>
              <el-button size="small" @click="changeMonth(1)">
                下个月 <i class="el-icon-arrow-right"></i>
              </el-button>
            </el-button-group>
          </div>
        </div>
        <div class="panel-content">
          <div class="calendar-header">
            <h2 class="current-month">{{ currentYear }}年{{ currentMonth }}月</h2>
          </div>
          <div class="calendar-grid">
            <div class="calendar-weekdays">
              <div class="weekday" v-for="day in ['日', '一', '二', '三', '四', '五', '六']" :key="day">
                {{ day }}
              </div>
            </div>
            <div class="calendar-days">
              <div
                v-for="(day, index) in calendarDays"
                :key="index"
                :class="['calendar-day', {
                  'other-month': day.isOtherMonth,
                  'today': day.isToday,
                  'has-appointment': day.hasAppointment
                }]"
                @click="viewDaySchedule(day)"
              >
                <div class="day-number">{{ day.day }}</div>
                <div class="day-appointments" v-if="day.appointments && day.appointments.length > 0">
                  <div class="appointment-dot" v-for="(apt, i) in day.appointments.slice(0, 3)" :key="i" :title="apt.topic">
                    <span class="dot-badge" :style="{ background: getAppointmentColor(apt.status) }"></span>
                    <span class="apt-time">{{ apt.time }}</span>
                  </div>
                  <div class="more-appointments" v-if="day.appointments.length > 3">
                    +{{ day.appointments.length - 3 }}
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 当日预约详情 -->
      <div class="panel" style="margin-top: 20px;" v-if="selectedDaySchedule.length > 0">
        <div class="panel-header">
          <h3>{{ selectedDate }} 日程安排</h3>
          <el-tag>{{ selectedDaySchedule.length }} 条预约</el-tag>
        </div>
        <div class="panel-content">
          <el-timeline>
            <el-timeline-item
              v-for="schedule in selectedDaySchedule"
              :key="schedule.id"
              :timestamp="schedule.time"
              placement="top"
              :color="getAppointmentColor(schedule.status)"
            >
              <el-card>
                <div class="schedule-item">
                  <div class="schedule-header">
                    <h4>{{ schedule.topic }}</h4>
                    <el-tag :type="getAppointmentStatusTagType(schedule.status)" size="small">
                      {{ formatAppointmentStatus(schedule.status) }}
                    </el-tag>
                  </div>
                  <div class="schedule-info">
                    <p><strong>患者姓名：</strong>{{ schedule.patientName }}</p>
                    <p><strong>联系方式：</strong>{{ schedule.contactInfo }}</p>
                    <p v-if="schedule.description"><strong>咨询说明：</strong>{{ schedule.description }}</p>
                  </div>
                </div>
              </el-card>
            </el-timeline-item>
          </el-timeline>
        </div>
      </div>
    </div>

    <!-- 数据统计分析 -->
    <div class="statistics-content" v-show="activeTab === 'statistics'">
      <el-row :gutter="20">
        <!-- 收入趋势图 -->
        <el-col :span="12">
          <div class="panel chart-panel">
            <div class="panel-header">
              <h3>咨询收入趋势</h3>
              <el-select v-model="incomeChartRange" size="small" style="width: 120px;">
                <el-option label="近7天" value="week"></el-option>
                <el-option label="近30天" value="month"></el-option>
                <el-option label="近90天" value="quarter"></el-option>
              </el-select>
            </div>
            <div class="panel-content">
              <div class="chart-container" ref="incomeChart" style="height: 350px;"></div>
            </div>
          </div>
        </el-col>

        <!-- 咨询类型分布 -->
        <el-col :span="12">
          <div class="panel chart-panel">
            <div class="panel-header">
              <h3>咨询类型分布</h3>
              <el-tag>累计咨询{{ stats.consultationCount }}次</el-tag>
            </div>
            <div class="panel-content">
              <div class="chart-container" ref="typeChart" style="height: 350px;"></div>
            </div>
          </div>
        </el-col>
      </el-row>

      <el-row :gutter="20" style="margin-top: 20px;">
        <!-- 时长统计图 -->
        <el-col :span="12">
          <div class="panel chart-panel">
            <div class="panel-header">
              <h3>咨询时长统计</h3>
            </div>
            <div class="panel-content">
              <div class="chart-container" ref="durationChart" style="height: 350px;"></div>
            </div>
          </div>
        </el-col>

        <!-- 评分分布图 -->
        <el-col :span="12">
          <div class="panel chart-panel">
            <div class="panel-header">
              <h3>患者满意度评分</h3>
              <div class="rating-summary">
                <el-rate v-model="stats.avgRating" disabled show-score text-color="#ff9900"></el-rate>
              </div>
            </div>
            <div class="panel-content">
              <div class="chart-container" ref="ratingChart" style="height: 350px;"></div>
            </div>
          </div>
        </el-col>
      </el-row>

      <!-- 统计明细表 -->
      <div class="panel" style="margin-top: 20px;">
        <div class="panel-header">
          <h3>统计数据明细</h3>
          <el-button size="small" type="primary">
            <i class="el-icon-download"></i> 导出统计数据
          </el-button>
        </div>
        <div class="panel-content">
          <el-table :data="statisticsTableData" style="width: 100%">
            <el-table-column prop="date" label="日期" width="120"></el-table-column>
            <el-table-column prop="consultationCount" label="咨询次数" width="100"></el-table-column>
            <el-table-column prop="duration" label="总时长(分钟)" width="120"></el-table-column>
            <el-table-column prop="income" label="收入(元)" width="100"></el-table-column>
            <el-table-column prop="avgRating" label="平均评分" width="100">
              <template #default="scope">
                <el-rate v-model="scope.row.avgRating" disabled size="small"></el-rate>
              </template>
            </el-table-column>
            <el-table-column prop="patientCount" label="服务患者数" width="120"></el-table-column>
          </el-table>
        </div>
      </div>
    </div>

    <!-- 咨询笔记 -->
    <div class="notes-content" v-show="activeTab === 'notes'">
      <el-row :gutter="20">
        <!-- 患者列表 -->
        <el-col :span="8">
          <div class="panel">
            <div class="panel-header">
              <h3>患者列表</h3>
              <el-input
                v-model="noteSearchKeyword"
                placeholder="搜索患者姓名"
                size="small"
                clearable
                prefix-icon="el-icon-search"
              ></el-input>
            </div>
            <div class="panel-content notes-patient-list">
              <div
                v-for="patient in filteredPatientList"
                :key="patient.id"
                :class="['patient-item', { active: selectedPatientForNotes?.id === patient.id }]"
                @click="selectPatientForNotes(patient)"
              >
                <div class="patient-avatar">
                  <el-avatar :size="40">{{ patient.name.substring(0, 1) }}</el-avatar>
                </div>
                <div class="patient-basic-info">
                  <div class="patient-name">{{ patient.name }}</div>
                  <div class="patient-meta">
                    {{ patient.age }}岁 · {{ patient.consultationCount }}次咨询
                  </div>
                </div>
                <div class="note-count">
                  <el-badge :value="getPatientNoteCount(patient.id)" :max="99">
                    <i class="el-icon-document"></i>
                  </el-badge>
                </div>
              </div>
            </div>
          </div>
        </el-col>

        <!-- 笔记列表 -->
        <el-col :span="16">
          <div class="panel" v-if="selectedPatientForNotes">
            <div class="panel-header">
              <h3>{{ selectedPatientForNotes.name }} 的咨询笔记</h3>
              <el-button type="primary" size="small" @click="showAddNoteDialog = true">
                <i class="el-icon-plus"></i> 新建笔记
              </el-button>
            </div>
            <div class="panel-content notes-list">
              <div v-if="currentPatientNotes.length === 0" class="empty-state">
                <i class="el-icon-document-remove"></i>
                <p>暂无咨询笔记</p>
              </div>
              <div v-else class="note-items">
                <div
                  v-for="note in currentPatientNotes"
                  :key="note.id"
                  class="note-item"
                >
                  <div class="note-header">
                    <div class="note-date">
                      <i class="el-icon-time"></i>
                      {{ formatTime(note.createTime) }}
                    </div>
                    <el-dropdown @command="handleNoteCommand">
                      <el-button type="text" size="small">
                        <i class="el-icon-more"></i>
                      </el-button>
                      <template #dropdown>
                        <el-dropdown-menu>
                          <el-dropdown-item :command="{ action: 'edit', note }">
                            <i class="el-icon-edit"></i> 编辑
                          </el-dropdown-item>
                          <el-dropdown-item :command="{ action: 'delete', note }">
                            <i class="el-icon-delete"></i> 删除
                          </el-dropdown-item>
                        </el-dropdown-menu>
                      </template>
                    </el-dropdown>
                  </div>
                  <div class="note-title">{{ note.title }}</div>
                  <div class="note-content">{{ note.content }}</div>
                  <div class="note-tags" v-if="note.tags && note.tags.length > 0">
                    <el-tag
                      v-for="tag in note.tags"
                      :key="tag"
                      size="small"
                      type="info"
                    >{{ tag }}</el-tag>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div class="panel empty-selection" v-else>
            <div class="empty-state">
              <i class="el-icon-user"></i>
              <p>请选择左侧患者后查看或编辑笔记</p>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 新增/编辑笔记弹窗 -->
    <el-dialog
      v-model="showAddNoteDialog"
      :title="editingNote ? '编辑笔记' : '新增咨询笔记'"
      width="700px"
      @close="resetNoteForm"
    >
      <el-form :model="noteForm" label-width="80px">
        <el-form-item label="笔记标题">
          <el-input v-model="noteForm.title" placeholder="请输入笔记标题"></el-input>
        </el-form-item>
        <el-form-item label="笔记内容">
          <el-input
            v-model="noteForm.content"
            type="textarea"
            :rows="10"
            placeholder="请记录本次咨询的重要信息、患者状态、干预方案等..."
          ></el-input>
        </el-form-item>
        <el-form-item label="标签">
          <el-tag
            v-for="tag in noteForm.tags"
            :key="tag"
            closable
            @close="removeNoteTag(tag)"
            style="margin-right: 8px;"
          >{{ tag }}</el-tag>
          <el-input
            v-if="inputTagVisible"
            ref="tagInput"
            v-model="inputTagValue"
            size="small"
            style="width: 100px;"
            @keyup.enter="handleInputConfirm"
            @blur="handleInputConfirm"
          ></el-input>
          <el-button v-else size="small" @click="showTagInput">+ 添加标签</el-button>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddNoteDialog = false">取消</el-button>
        <el-button type="primary" @click="saveNote" :loading="savingNote">保存</el-button>
      </template>
    </el-dialog>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  TeamOutlined,
  MessageOutlined,
  StarOutlined,
  WalletOutlined
} from '@ant-design/icons-vue'
import { getPsychologists, sendConsultationMessage, getConsultationMessages, getPsychologistPatients, getPsychologistStats, getConsultationDetail, updateConsultationStatus, getPsychologistDetail, uploadPsychologistAvatar } from '@/api/consultation'
import { getPsychologistAppointments, acceptAppointment, cancelAppointment, getPsychologistIncomeTrend, getPsychologistConsultationTypes, getPsychologistDurationStats, getPsychologistRatingDistribution, getPsychologistDetailedStats } from '@/api/consultation'
import * as echarts from 'echarts'

const router = useRouter()

// 顶部标签
const activeTab = ref('main')

// 头像相关工具方法
const API_BASE = 'http://localhost:8080'
const resolveAvatarUrl = (path) => {
  if (!path) return ''
  if (/^https?:\/\//.test(path) || path.startsWith('data:')) return path
  if (path.startsWith('/')) return API_BASE + path
  return `${API_BASE}/${path}`
}
const defaultAvatar = `${API_BASE}/uploads/avatars/default.jpg`
const avatarUploading = ref(false)

const beforeAvatarUpload = (file) => {
  const isImage = file.type && file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2
  if (!isImage) ElMessage.error('请上传图片文件')
  if (!isLt2M) ElMessage.error('图片大小不能超过2MB')
  return isImage && isLt2M
}

const doAvatarUpload = async (options) => {
  const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
  const id = userInfo.id
  const file = options.file
  try {
    avatarUploading.value = true
    const formData = new FormData()
    formData.append('id', id)
    formData.append('file', file)
    const res = await uploadPsychologistAvatar(formData)
    if (res.success && res.url) {
      psychologistInfo.value.avatar = res.url
      // ?????????
    } else {
      ElMessage.error(res.message || '头像上传失败')
    }
  } catch (e) {
    ElMessage.error('头像上传失败')
  } finally {
    avatarUploading.value = false
  }
}

// ???????
const stats = ref({
  patientCount: 0,
  consultationCount: 0,
  avgRating: 0,
  totalIncome: 0,
  monthlyIncome: 0
})

// ??????
const patientList = ref([])

// ????????????????
const activeConsultations = ref([])
const consultationsLoading = ref(false)

// ???????
const appointmentList = ref([])
const appointmentLoading = ref(false)

// ???????????
const showChatDialog = ref(false)
const chatMessages = ref([])
const newMessage = ref('')
const sending = ref(false)
const completing = ref(false)
const currentConsultation = ref(null)

// ??????????????
const showDetailDialog = ref(false)
const currentConsultationDetail = ref(null)

// ????????????
const currentPsychologistName = ref('未知专家')

// ??????????????
const loadPsychologistStats = async () => {
  try {
    // ?????????????ID
    const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
    const psychologistId = userInfo.id
    
    if (psychologistId) {
      const response = await getPsychologistStats(psychologistId)
      if (response.success) {
        // ???????????????
        stats.value.patientCount = response.stats.patientCount || 0
        stats.value.consultationCount = response.stats.consultationCount || 0
        stats.value.avgRating = response.stats.avgRating || 0
        stats.value.totalIncome = response.stats.totalIncome || 0
        console.log('???????????????:', response.stats)
      } else {
        ElMessage.warning('暂时无法获取统计数据，已使用默认值')
        // ??????????
        stats.value.patientCount = 0
        stats.value.consultationCount = 0
        stats.value.avgRating = 0
        stats.value.totalIncome = 0
      }
    }
  } catch (error) {
    console.error('?????????????????:', error)
    ElMessage.error('加载统计数据失败')
    // ??????????
    stats.value.patientCount = 0
    stats.value.consultationCount = 0
    stats.value.avgRating = 0
    stats.value.monthlyIncome = 0
  }
}

// ?????????
const loadPatientList = async () => {
  try {
    // ?????????????ID
    const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
    const psychologistId = userInfo.id
    
    if (psychologistId) {
      // ??????????????????????????????
      const response = await getPsychologistPatients(psychologistId)
      if (response.success) {
        // ?????????????????????????????????
        patientList.value = response.patients.map(patient => ({
          id: patient.id,
          name: patient.name,
          age: patient.age,
          lastConsultation: patient.lastConsultation || patient.createTime, // ?????????
          scheduledTime: patient.scheduledTime, // ?????
          status: patient.status,
          consultationCount: patient.consultationCount,
          consultationId: patient.consultationId, // ???????ID?????????
          startTime: patient.startTime, // ??????
          endTime: patient.endTime // ???????
        }))
        
        console.log('??????????????? ' + patientList.value.length + ' ?????')
      }
    }
  } catch (error) {
    console.error('????????????:', error)
    ElMessage.error('加载患者列表失败')
  }
}

// ??????????
const openChat = (consultation) => {
  currentConsultation.value = consultation
  showChatDialog.value = true
  loadChatMessages(consultation.id)
}

// ???????????
const loadChatMessages = async (consultationId) => {
  try {
    const response = await getConsultationMessages(consultationId)
    if (response.success) {
      chatMessages.value = response.data || []
    } else {
      // ??????????????
      chatMessages.value = []
    }
  } catch (error) {
    console.error('??????????????:', error)
    // ??????????????
    chatMessages.value = []
  }
}

// ???????????????
const sendMessage = async () => {
  if (!newMessage.value.trim()) {
    ElMessage.warning('请输入消息内容')
    return
  }

  sending.value = true
  try {
    // ???chatMessages??????
    if (!Array.isArray(chatMessages.value)) {
      chatMessages.value = []
    }
    
    // ???????????????????????
    const newMsg = {
      id: Date.now(),
      consultationId: currentConsultation.value.id,
      messageContent: newMessage.value.trim(),
      senderType: 'psychologist',
      createTime: new Date().toISOString()
    }
    
    // ?????????????
    chatMessages.value = [...chatMessages.value, newMsg]
    // ?????????
    newMessage.value = ''
    
    // ?????????3???
    setTimeout(() => {
      const patientReply = {
        id: Date.now() + 1,
        consultationId: currentConsultation.value.id,
          messageContent: '好的，我想先聊聊最近的压力和睡眠问题。',
        senderType: 'user',
        createTime: new Date().toISOString()
      }
      chatMessages.value = [...chatMessages.value, patientReply]
    }, 3000)
    
  } catch (error) {
    console.error('??????????:', error)
    ElMessage.error('发送消息失败')
  } finally {
    sending.value = false
  }
}

// ???????????
const handleCloseChat = () => {
  showChatDialog.value = false
  currentConsultation.value = null
  chatMessages.value = []
  newMessage.value = ''
}

// ??????????????
const completeConsultation = async () => {
  try {
    if (!currentConsultation.value) {
      ElMessage.error('当前咨询不存在')
      return
    }
    
    completing.value = true
    
    // ?????????????
    await new Promise(resolve => setTimeout(resolve, 1000)) // ???1?????
    
    // ?????????
    
    // ???????
    handleCloseChat()
    
  } catch (error) {
    console.error('?????????:', error)
    ElMessage.error('完成咨询失败')
  } finally {
    completing.value = false
  }
}

// ????????
const formatTime = (time) => {
  if (!time) return '-'
  return new Date(time).toLocaleString('zh-CN')
}

const formatDateTime = (time) => {
  if (!time) return '-'
  return new Date(time).toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const formatMessageTime = (time) => {
  return new Date(time).toLocaleString('zh-CN', {
    hour: '2-digit',
    minute: '2-digit'
  })
}

// ??????????????
const canCompleteConsultation = (patient) => {
  // ????????????????????
  if (patient.status === '已完成') {
    return false
  }
  
  // ?????????"??????"?????????
  if (patient.status !== '进行中') {
    return false
  }
  
  // ????????????n???????????????
  if (patient.scheduledTime) {
    const scheduledTime = new Date(patient.scheduledTime)
    const now = new Date()
    // ????????????????????
    return now >= scheduledTime
  }
  
  // ????????????????????
  return true
}

// ????????????
const getCompleteButtonText = (patient) => {
  if (patient.status === '已完成') {
    return '已完成'
  }
  
  if (patient.status !== '进行中') {
    return '不可完成'
  }
  
  if (patient.scheduledTime) {
    const scheduledTime = new Date(patient.scheduledTime)
    const now = new Date()
    if (now < scheduledTime) {
      return '时间未到'
    }
  }
  
  return '完成咨询'
}

// ??????
const getAvatar = (senderType) => {
  return senderType === 'psychologist' 
    ? '/avatars/psychologist.jpg' 
    : '/avatars/patient.jpg'
}

// ?????????????????????
const loadActiveConsultations = async () => {
  consultationsLoading.value = true
  try {
    const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
    const psychologistId = userInfo.id
    
    if (psychologistId) {
      const response = await getPsychologistPatients(psychologistId)
      if (response.success) {
        // ???????????????????????????????????
        activeConsultations.value = response.patients.map(patient => ({
          id: patient.consultationId,
          patientName: patient.name,
          title: patient.problem || '心理咨询',
          status: patient.status,
          createTime: patient.createTime || patient.lastConsultation,
          userFeedback: patient.userFeedback,
          userRating: patient.userRating || 0,
          description: patient.description
        }))
        
        console.log('?????????????? ' + activeConsultations.value.length + ' ?????')
      }
    }
  } catch (error) {
    console.error('????????????:', error)
    ElMessage.error('????????????')
  } finally {
    consultationsLoading.value = false
  }
}

// ?????????????
const viewConsultationFeedback = async (consultation) => {
  try {
    const detailResponse = await getConsultationDetail(consultation.id)
    if (detailResponse.success) {
      currentConsultationDetail.value = detailResponse.consultation
      showDetailDialog.value = true
    } else {
      ElMessage.error('获取咨询详情失败')
    }
  } catch (error) {
    console.error('????????????:', error)
    ElMessage.error('?????????')
  }
}

// ???????????????????????
const completeConsultationDirect = async (consultation) => {
  try {
    await ElMessageBox.confirm(
      `确认将 ${consultation.patientName} 的咨询标记为已完成吗？此操作会更新统计数据。`,
      '确认完成咨询',
      {
        confirmButtonText: '确认完成',
        cancelButtonText: '取消',
        type: 'warning',
      }
    )
    
    // ????????????????????
    const startTime = consultation.startTime ? new Date(consultation.startTime) : new Date(consultation.createTime)
    const endTime = new Date()
    const durationMinutes = Math.max(1, Math.round((endTime - startTime) / (1000 * 60)))
    
    // ???????????API
    const response = await completeConsultation({
      consultationId: consultation.id,
      duration: durationMinutes,
      rating: 5, // ???????
      feedback: '咨询已结束，自动记录' // 默认反馈
    })
    
    if (response.success) {
      // ?????????
      // ?????????????????
      await loadActiveConsultations()
      await loadPatientList()
      await loadPsychologistStats()
      
      // ?????????????
      await loadStatisticsData()
      if (activeTab.value === 'statistics') {
        initCharts()
      }
    } else {
      ElMessage.error('完成咨询失败: ' + response.message)
    }
  } catch (error) {
    if (error === 'cancel') {
      // ??????????????
      return
    }
    console.error('?????????:', error)
    ElMessage.error('完成咨询失败: ' + error.message)
  }
}

// ????????
const refreshConsultations = () => {
  loadActiveConsultations()
  ElMessage.success('咨询列表已刷新')
}

// ??????????
const viewPatientDetail = async (patient) => {
  try {
    // ???????????consultationId????????
    const consultationId = patient.consultationId || patient.id
    console.log('???????????????:', patient, '???ID:', consultationId)
    
    if (!consultationId) {
      ElMessage.error('无法获取咨询ID，无法查看详情')
      return
    }
    
    const detailResponse = await getConsultationDetail(consultationId)
    if (detailResponse.success) {
      currentConsultationDetail.value = detailResponse.consultation
      showDetailDialog.value = true
      console.log('?????????????? - ???ID:', detailResponse.consultation.id, 
                  '???ID:', detailResponse.consultation.userId,
                  '????:', detailResponse.consultation.title)
    } else {
      ElMessage.error('获取咨询详情失败: ' + detailResponse.message)
    }
  } catch (error) {
    console.error('????????????:', error)
    ElMessage.error('查看详情失败: ' + error.message)
  }
}

// ????????????
const updatePatientStatus = async (patient, newStatus) => {
  try {
    const consultationId = patient.consultationId || patient.id
    if (!consultationId) {
      ElMessage.error('?????????ID')
      return
    }
    
    // ??????????????????????????
    if (newStatus === '已完成') {
      if (!canCompleteConsultation(patient)) {
        if (patient.status !== '进行中') {
          ElMessage.warning('?????????????????')
        } else if (patient.scheduledTime) {
          const scheduledTime = new Date(patient.scheduledTime)
          const now = new Date()
          if (now < scheduledTime) {
            ElMessage.warning('??????????????????')
          }
        }
        return
      }
    }
    
    const response = await updateConsultationStatus(consultationId, newStatus)
    
    if (response.success) {
      // ?????????
      // ????????????????????
      await loadPatientList()
      await loadPsychologistStats()
      
      // ?????????????
      await loadStatisticsData()
      if (activeTab.value === 'statistics') {
        initCharts()
      }
    } else {
      ElMessage.error('?????????: ' + response.message)
    }
  } catch (error) {
    console.error('????????????:', error)
    ElMessage.error('?????????: ' + error.message)
  }
}

// ???????????
const handleCloseDetail = () => {
  showDetailDialog.value = false
  currentConsultationDetail.value = null
}

// ????????????
const getStatusTagType = (status) => {
  switch (status) {
    case '待确认': return 'warning'
    case '进行中': return 'success'
    case '已完成': return 'info'
    case '已取消': return 'danger'
    default: return 'info'
  }
}

// ??????????
const handleBackToLogin = () => {
  router.push('/login')
}

// ???????????
const loadAppointmentList = async () => {
  appointmentLoading.value = true
  try {
    // ?????????????ID
    const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
    const psychologistId = userInfo.id
    
    if (psychologistId) {
      const response = await getPsychologistAppointments(psychologistId)
      if (response.success) {
        appointmentList.value = response.appointments || []
        console.log('????????????:', appointmentList.value)
      } else {
        ElMessage.error('??????????: ' + response.message)
        // ?????????????????
        appointmentList.value = [
          {
            id: 1,
            topic: '???????????',
            preferredTime: new Date().toISOString(),
            contactInfo: '138****1234',
            status: '??????'
          },
          {
            id: 2,
            topic: '?????????',
            preferredTime: new Date(Date.now() + 86400000).toISOString(),
            contactInfo: '???: user123',
            status: '??????'
          }
        ]
      }
    } else {
      ElMessage.warning('????????ID?????????????')
    }
  } catch (error) {
    console.error('??????????????:', error)
    ElMessage.error('???????????: ' + error.message)
    // ?????????????????
    appointmentList.value = [
      {
        id: 1,
        topic: '???????????',
        preferredTime: new Date().toISOString(),
        contactInfo: '138****1234',
        status: '??????'
      },
      {
        id: 2,
        topic: '?????????',
        preferredTime: new Date(Date.now() + 86400000).toISOString(),
        contactInfo: '???: user123',
        status: '??????'
      }
    ]
  } finally {
    appointmentLoading.value = false
  }
}

// ??????????
const handleAcceptAppointment = async (appointment) => {
  try {
    ElMessage.info('?????????????...')
    
    const response = await acceptAppointment({
      appointmentId: appointment.id
    })
    
    if (response.success) {
      // ?????????
      
      // ?????????????
      loadAppointmentList()
      loadPatientList()
    } else {
      ElMessage.error('??????: ' + response.message)
    }
  } catch (error) {
    console.error('??????????:', error)
    ElMessage.error('??????: ' + error.message)
  }
}

// ?????????????
const handleCancelAppointment = async (appointment) => {
  try {
    // ??????????
    await ElMessageBox.confirm(
      '????????????????????????????',
      '??????',
      {
        confirmButtonText: '???',
        cancelButtonText: '???',
        type: 'warning',
      }
    )
    
    ElMessage.info('?????????...')
    
    const response = await cancelAppointment({
      appointmentId: appointment.id
    })
    
    if (response.success) {
      // ????????
      
      // ???????
      loadAppointmentList()
    } else {
      ElMessage.error('??????: ' + response.message)
    }
  } catch (error) {
    if (error === 'cancel') {
      // ??????????????
      return
    }
    console.error('????????????:', error)
    ElMessage.error('??????: ' + error.message)
  }
}

// ??????????????
const getAppointmentStatusTagType = (status) => {
  switch (status) {
    case 'pending': return 'warning'
    case 'confirmed': return 'success'
    case 'completed': return 'info'
    case 'cancelled': return 'danger'
    default: return 'info'
  }
}

// ????????????
const formatAppointmentStatus = (status) => {
  switch (status) {
    case 'pending': return '待确认'
    case 'confirmed': return '已确认'
    case 'completed': return '已完成'
    case 'cancelled': return '已取消'
    default: return status
  }
}

// ??????????
const psychologistInfo = ref({
  name: '',
  title: '',
  qualifications: [],
  avatar: '',
  experience: '',
  specialization: '',
  education: '',
  certification: '',
  introduction: '',
  hourlyRate: 0,
  totalPatients: 0,
  successRate: 0,
  languages: [],
  availableTime: ''
})

// ????????????
const loadCurrentPsychologistInfo = async () => {
  try {
    const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
    currentPsychologistName.value = userInfo.realName || userInfo.username || '未知专家'
    
    // ???????????????????
    const psychologistId = userInfo.id
    if (psychologistId) {
      const response = await getPsychologistDetail(psychologistId)
      console.log('???????API???:', response) // ?????????
      if (response.success && response.psychologist) {
        console.log('???????API???????????:', response) // ?????????????????
        console.log('??????????????:', response.psychologist) // ????????????????
        console.log('???????:', response.psychologist.realName) // ??????????????
        console.log('??????:', response.psychologist.title) // ????????????
        console.log('????:', response.psychologist.hourlyRate) // ???????????
        
        // ??????????????????????????????????????
        psychologistInfo.value = {
          name: response.psychologist.realName || response.psychologist.username || '心理专家',
          title: response.psychologist.title || '国家二级心理咨询师',
          qualifications: ['执业证书', '心理咨询师资格认证'], // 默认资质标签
          avatar: response.psychologist.avatar || '',
          experience: response.psychologist.experienceYears ? response.psychologist.experienceYears + '年临床经验' : '8年临床经验',
          specialization: response.psychologist.specialty || '焦虑疏导、亲密关系、职场压力',
          education: '心理学硕士', // 默认学历展示
          certification: '国家认证心理咨询师', // 默认认证信息
          introduction: response.psychologist.introduction || '擅长为来访者提供情绪支持与结构化干预，帮助建立更稳定的心理状态。',
          hourlyRate: response.psychologist.hourlyRate || 300,
          totalPatients: 0, // ????????
          successRate: 0, // ????????
          languages: ['中文', '英文'], // 默认服务语言
          availableTime: '工作日 9:00-18:00' // 默认可预约时间
        }
        
        console.log('???????????????:', psychologistInfo.value) // ?????????????????
      } else {
        console.log('API??????????????????????')
        // API???????????????????????
        psychologistInfo.value = {
          name: currentPsychologistName.value,
          title: '',
          qualifications: [],
          avatar: '',
          experience: '',
          specialization: '',
          education: '',
          certification: '',
          introduction: '',
          hourlyRate: 0,
          totalPatients: 0,
          successRate: 0,
          languages: [],
          availableTime: ''
        }
      }
    } else {
      console.log('??????ID??????????')
      // ??????ID???????
      psychologistInfo.value = {
        name: currentPsychologistName.value,
        title: '',
        qualifications: [],
        avatar: '',
        experience: '',
        specialization: '',
        education: '',
        certification: '',
        introduction: '',
        hourlyRate: 0,
        totalPatients: 0,
        successRate: 0,
        languages: [],
        availableTime: ''
      }
    }
  } catch (error) {
    console.error('?????????????:', error)
    // ??????????????????????
    psychologistInfo.value = {
      name: currentPsychologistName.value,
      title: '',
      qualifications: [],
      avatar: '',
      experience: '',
      specialization: '',
      education: '',
      certification: '',
      introduction: '',
      hourlyRate: 0,
      totalPatients: 0,
      successRate: 0,
      languages: [],
      availableTime: ''
    }
  }
}

// ===== ????????? =====
const currentYear = ref(new Date().getFullYear())
const currentMonth = ref(new Date().getMonth() + 1)
const calendarDays = ref([])
const selectedDaySchedule = ref([])
const selectedDate = ref('')

// ????????????
const generateCalendarDays = () => {
  const year = currentYear.value
  const month = currentMonth.value
  const firstDay = new Date(year, month - 1, 1).getDay()
  const daysInMonth = new Date(year, month, 0).getDate()
  const daysInPrevMonth = new Date(year, month - 1, 0).getDate()
  
  const days = []
  
  // ?????????
  for (let i = firstDay - 1; i >= 0; i--) {
    days.push({
      day: daysInPrevMonth - i,
      isOtherMonth: true,
      isToday: false,
      hasAppointment: false,
      appointments: []
    })
  }
  
  // ????????
  const today = new Date()
  for (let i = 1; i <= daysInMonth; i++) {
    const currentDate = new Date(year, month - 1, i)
    const isToday = currentDate.toDateString() === today.toDateString()
    
    // ?????????
    const dayAppointments = appointmentList.value.filter(apt => {
      const aptDate = new Date(apt.preferredTime)
      return aptDate.getFullYear() === year &&
             aptDate.getMonth() === month - 1 &&
             aptDate.getDate() === i
    }).map(apt => ({
      id: apt.id,
      topic: apt.topic,
      time: new Date(apt.preferredTime).toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' }),
      status: apt.status,
      patientName: apt.contactInfo,
      contactInfo: apt.contactInfo,
      description: apt.description || ''
    }))
    
    days.push({
      day: i,
      date: `${year}-${String(month).padStart(2, '0')}-${String(i).padStart(2, '0')}`,
      isOtherMonth: false,
      isToday,
      hasAppointment: dayAppointments.length > 0,
      appointments: dayAppointments
    })
  }
  
  // ?????????
  const remainingDays = 42 - days.length
  for (let i = 1; i <= remainingDays; i++) {
    days.push({
      day: i,
      isOtherMonth: true,
      isToday: false,
      hasAppointment: false,
      appointments: []
    })
  }
  
  calendarDays.value = days
}

const changeMonth = (delta) => {
  currentMonth.value += delta
  if (currentMonth.value > 12) {
    currentMonth.value = 1
    currentYear.value++
  } else if (currentMonth.value < 1) {
    currentMonth.value = 12
    currentYear.value--
  }
  generateCalendarDays()
}

const goToToday = () => {
  const today = new Date()
  currentYear.value = today.getFullYear()
  currentMonth.value = today.getMonth() + 1
  generateCalendarDays()
}

const viewDaySchedule = (day) => {
  if (day.isOtherMonth || !day.hasAppointment) return
  selectedDate.value = day.date
  selectedDaySchedule.value = day.appointments
}

const getAppointmentColor = (status) => {
  const colors = {
    'pending': '#E6A23C',
    'confirmed': '#67C23A',
    'completed': '#909399',
    'cancelled': '#F56C6C'
  }
  return colors[status] || '#909399'
}

// ===== ?????????? =====
const incomeChartRange = ref('month')
const statisticsTableData = ref([])
const incomeChartData = ref(null)
const typeChartData = ref(null)
const durationChartData = ref(null)
const ratingChartData = ref(null)

const incomeChart = ref(null)
const typeChart = ref(null)
const durationChart = ref(null)
const ratingChart = ref(null)

// ???????????
const loadStatisticsData = async () => {
  try {
    const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
    const psychologistId = userInfo.id
    
    if (!psychologistId) {
      ElMessage.warning('????????ID')
      return
    }
    
    // ????????????????
    const incomeTrendRes = await getPsychologistIncomeTrend(psychologistId, incomeChartRange.value)
    if (incomeTrendRes.success) {
      incomeChartData.value = incomeTrendRes.data
    }
    
    // ?????????????
    const typesRes = await getPsychologistConsultationTypes(psychologistId)
    if (typesRes.success) {
      typeChartData.value = typesRes.data
    }
    
    // ?????????????
    const durationRes = await getPsychologistDurationStats(psychologistId)
    if (durationRes.success) {
      durationChartData.value = durationRes.data
    }
    
    // ??????????
    const ratingRes = await getPsychologistRatingDistribution(psychologistId)
    if (ratingRes.success) {
      ratingChartData.value = ratingRes.data
    }
    
    // ?????????????
    const detailedRes = await getPsychologistDetailedStats(psychologistId, {
      startDate: new Date(Date.now() - 7 * 24 * 60 * 60 * 1000).toISOString().split('T')[0],
      endDate: new Date().toISOString().split('T')[0]
    })
    if (detailedRes.success) {
      statisticsTableData.value = detailedRes.data || []
    }
    
    console.log('????????????')
  } catch (error) {
    console.error('??????????????:', error)
    ElMessage.error('??????????????')
  }
}

// ????????
const initCharts = () => {
  nextTick(() => {
    initIncomeChart()
    initTypeChart()
    initDurationChart()
    initRatingChart()
  })
}

const initIncomeChart = () => {
  if (!incomeChart.value) return
  
  // ?????????????
  let chart = echarts.getInstanceByDom(incomeChart.value)
  if (!chart) {
    chart = echarts.init(incomeChart.value)
  }
  
  // ??????????
  const xAxisData = incomeChartData.value?.dates || []
  const seriesData = incomeChartData.value?.incomes || []
  
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' },
      formatter: '{b}<br/>????: ??{c}'
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: xAxisData,
      axisLine: { lineStyle: { color: '#999' } }
    },
    yAxis: {
      type: 'value',
      axisLine: { lineStyle: { color: '#999' } },
      axisLabel: {
        formatter: '??{value}'
      }
    },
    series: [{
      name: '????',
      type: 'line',
      smooth: true,
      data: seriesData,
      // ????y???????????????????????????
      areaStyle: { color: 'rgba(37, 99, 235, 0.12)' },
      itemStyle: { color: '#2563eb' },
      lineStyle: { width: 3 }
    }]
  }
  chart.setOption(option, true) // ?????????true??????????????I
}

const initTypeChart = () => {
  if (!typeChart.value) return
  
  // ?????????????
  let chart = echarts.getInstanceByDom(typeChart.value)
  if (!chart) {
    chart = echarts.init(typeChart.value)
  }
  
  // ??????????
  const chartData = typeChartData.value || []
  const colors = ['#667eea', '#764ba2', '#f093fb', '#4facfe', '#43e97b', '#ffa502', '#ff6348']
  const seriesData = chartData.map((item, index) => ({
    value: item.count,
    name: item.type,
    itemStyle: { color: colors[index % colors.length] }
  }))
  
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c}?? ({d}%)'
    },
    legend: {
      orient: 'vertical',
      left: 'left'
    },
    series: [{
      name: '???????',
      type: 'pie',
      radius: ['40%', '70%'],
      avoidLabelOverlap: false,
      itemStyle: {
        borderRadius: 10,
        borderColor: '#fff',
        borderWidth: 2
      },
      label: {
        show: true,
        formatter: '{b}: {d}%'
      },
      data: seriesData
    }]
  }
  chart.setOption(option, true)
}

const initDurationChart = () => {
  if (!durationChart.value) return
  
  // ?????????????
  let chart = echarts.getInstanceByDom(durationChart.value)
  if (!chart) {
    chart = echarts.init(durationChart.value)
  }
  
  // ??????????
  const chartData = durationChartData.value || []
  const xAxisData = chartData.map(item => item.range)
  const seriesData = chartData.map(item => item.count)
  
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' },
      formatter: '{b}<br/>???????: {c}??'
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: xAxisData.length > 0 ? xAxisData : ['30????????', '30-60????', '60-90????', '90????????'],
      axisLine: { lineStyle: { color: '#999' } }
    },
    yAxis: {
      type: 'value',
      axisLine: { lineStyle: { color: '#999' } },
      axisLabel: {
        formatter: '{value}??'
      }
    },
    series: [{
      name: '???????',
      type: 'bar',
      data: seriesData.length > 0 ? seriesData : [0, 0, 0, 0],
      itemStyle: {
        // ????y?????????
        color: '#2563eb',
        borderRadius: [5, 5, 0, 0]
      }
    }]
  }
  chart.setOption(option, true)
}

const initRatingChart = () => {
  if (!ratingChart.value) return
  
  // ?????????????
  let chart = echarts.getInstanceByDom(ratingChart.value)
  if (!chart) {
    chart = echarts.init(ratingChart.value)
  }
  
  // ??????????
  const chartData = ratingChartData.value || []
  const xAxisData = chartData.map(item => `${item.rating}??`)
  const seriesData = chartData.map(item => item.count)
  
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' },
      formatter: '{b}<br/>????????: {c}??'
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: xAxisData.length > 0 ? xAxisData : ['5??', '4??', '3??', '2??', '1??'],
      axisLine: { lineStyle: { color: '#999' } }
    },
    yAxis: {
      type: 'value',
      axisLine: { lineStyle: { color: '#999' } },
      axisLabel: {
        formatter: '{value}??'
      }
    },
    series: [{
      name: '????????',
      type: 'bar',
      data: seriesData.length > 0 ? seriesData : [0, 0, 0, 0, 0],
      itemStyle: {
        color: '#43e97b',
        borderRadius: [5, 5, 0, 0]
      }
    }]
  }
  chart.setOption(option, true)
}

// ???????????????????????????????
watch(activeTab, async (newVal) => {
  if (newVal === 'statistics') {
    await loadStatisticsData()
    initCharts()
  } else if (newVal === 'calendar') {
    generateCalendarDays()
  }
})

// ??????????????????????????
watch(incomeChartRange, async () => {
  if (activeTab.value === 'statistics') {
    const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
    const psychologistId = userInfo.id
    
    if (psychologistId) {
      try {
        const incomeTrendRes = await getPsychologistIncomeTrend(psychologistId, incomeChartRange.value)
        if (incomeTrendRes.success) {
          incomeChartData.value = incomeTrendRes.data
          initIncomeChart()
        }
      } catch (error) {
        console.error('???????????????????:', error)
      }
    }
  }
})

// ===== ????????? =====
const noteSearchKeyword = ref('')
const selectedPatientForNotes = ref(null)
const showAddNoteDialog = ref(false)
const editingNote = ref(null)
const savingNote = ref(false)
const inputTagVisible = ref(false)
const inputTagValue = ref('')
const tagInput = ref(null)

const noteForm = ref({
  title: '',
  content: '',
  tags: []
})

// ????????????????????????????????
const patientNotes = ref({})

const currentPatientNotes = computed(() => {
  if (!selectedPatientForNotes.value) return []
  return patientNotes.value[selectedPatientForNotes.value.id] || []
})

const filteredPatientList = computed(() => {
  if (!noteSearchKeyword.value) return patientList.value
  return patientList.value.filter(p => 
    p.name.toLowerCase().includes(noteSearchKeyword.value.toLowerCase())
  )
})

const selectPatientForNotes = (patient) => {
  selectedPatientForNotes.value = patient
}

const getPatientNoteCount = (patientId) => {
  return (patientNotes.value[patientId] || []).length
}

const saveNote = () => {
  if (!noteForm.value.title || !noteForm.value.content) {
    ElMessage.warning('?????????????')
    return
  }
  
  savingNote.value = true
  
  setTimeout(() => {
    const patientId = selectedPatientForNotes.value.id
    
    if (editingNote.value) {
      // ?????
      const noteIndex = patientNotes.value[patientId].findIndex(n => n.id === editingNote.value.id)
      if (noteIndex !== -1) {
        patientNotes.value[patientId][noteIndex] = {
          ...editingNote.value,
          ...noteForm.value
        }
      }
      // ????????
    } else {
      // ??????
      const newNote = {
        id: Date.now(),
        patientId,
        ...noteForm.value,
        createTime: new Date().toISOString()
      }
      
      if (!patientNotes.value[patientId]) {
        patientNotes.value[patientId] = []
      }
      patientNotes.value[patientId].unshift(newNote)
      // ?????????
    }
    
    showAddNoteDialog.value = false
    resetNoteForm()
    savingNote.value = false
  }, 500)
}

const resetNoteForm = () => {
  noteForm.value = {
    title: '',
    content: '',
    tags: []
  }
  editingNote.value = null
}

const handleNoteCommand = (command) => {
  if (command.action === 'edit') {
    editingNote.value = command.note
    noteForm.value = {
      title: command.note.title,
      content: command.note.content,
      tags: [...command.note.tags]
    }
    showAddNoteDialog.value = true
  } else if (command.action === 'delete') {
    ElMessageBox.confirm('????????????????', '??????', {
      confirmButtonText: '???',
      cancelButtonText: '???',
      type: 'warning'
    }).then(() => {
      const patientId = selectedPatientForNotes.value.id
      const noteIndex = patientNotes.value[patientId].findIndex(n => n.id === command.note.id)
      if (noteIndex !== -1) {
        patientNotes.value[patientId].splice(noteIndex, 1)
        // ?????????
      }
    }).catch(() => {})
  }
}

const showTagInput = () => {
  inputTagVisible.value = true
  nextTick(() => {
    tagInput.value?.focus()
  })
}

const handleInputConfirm = () => {
  if (inputTagValue.value) {
    noteForm.value.tags.push(inputTagValue.value)
  }
  inputTagVisible.value = false
  inputTagValue.value = ''
}

const removeNoteTag = (tag) => {
  noteForm.value.tags.splice(noteForm.value.tags.indexOf(tag), 1)
}

onMounted(async () => {
  await loadCurrentPsychologistInfo()
  loadPsychologistStats()
  loadPatientList()
  loadAppointmentList()
  loadActiveConsultations()
  generateCalendarDays()
})
</script>

<style lang="scss" scoped>
.dashboard-container {
  // ????? token??????y???
  --bg: #f8fafc;
  --surface: #ffffff;
  --surface-2: #f1f5f9;
  --border: rgba(15, 23, 42, 0.10);
  --text: #0f172a;
  --muted: #475569;
  --muted-2: #64748b;
  --primary: #2563eb;
  --danger: #ef4444;
  --success: #16a34a;
  --warning: #d97706;
  --radius-lg: 16px;
  --radius-md: 12px;
  --radius-sm: 10px;
  --shadow-1: 0 1px 2px rgba(15, 23, 42, 0.06), 0 6px 20px rgba(15, 23, 42, 0.06);
  --shadow-2: 0 2px 6px rgba(15, 23, 42, 0.08), 0 14px 40px rgba(15, 23, 42, 0.10);
  --ring: 0 0 0 3px rgba(37, 99, 235, 0.18);
}

.dashboard-container {
  // ????/??????????? .app-page ??????????????? 100vw hack
  width: 100%;
  max-width: none;
  margin-left: 0;
  margin-right: 0;
  padding: 0;
  background: transparent;
  min-height: auto;
  font-family: 'Segoe UI', 'Microsoft YaHei', -apple-system, BlinkMacSystemFont, sans-serif;
  color: var(--text);
}

// ?????????
.header-section {
  background: #0f172a;
  border-radius: var(--radius-lg);
  padding: 28px;
  margin-bottom: 22px;
  color: #e2e8f0;
  border: 1px solid rgba(148, 163, 184, 0.25);
  box-shadow: 0 16px 30px rgba(15, 23, 42, 0.18);
  position: relative;
  overflow: hidden;

  &::after {
    content: '';
    position: absolute;
    inset: 0;
    background: rgba(148, 163, 184, 0.06);
    pointer-events: none;
  }
  
  .expert-header {
    display: flex;
    align-items: center;
    gap: 24px;
    
    .expert-avatar {
      width: 80px;
      height: 80px;
      border-radius: 50%;
      background: rgba(148, 163, 184, 0.2);
      border: 2px solid rgba(148, 163, 184, 0.4);
      display: flex;
      align-items: center;
      justify-content: center;
      
      .avatar-placeholder {
        font-size: 32px;
        font-weight: bold;
        color: white;
      }
    }
    
    .expert-info {
      flex: 1;
      
      h2 {
        margin: 0 0 6px 0;
        font-size: 30px;
        font-weight: 800;
        color: #f8fafc;
        letter-spacing: 0.2px;
      }
      
      .expert-title {
        font-size: 14px;
        color: #94a3b8;
        margin-bottom: 10px;
        letter-spacing: 0.3px;
      }
      
      .expert-qualifications {
        display: flex;
        flex-wrap: wrap;
        gap: 8px;
        margin-bottom: 16px;
        
        .qualification {
          background: rgba(30, 41, 59, 0.9);
          border: 1px solid rgba(148, 163, 184, 0.35);
          color: #e2e8f0;
          padding: 3px 10px;
          border-radius: 20px;
          font-size: 12px;
          font-weight: 700;
        }
      }
      
      .expert-details {
        display: grid;
        grid-template-columns: repeat(2, 1fr);
        gap: 12px;
        margin-bottom: 16px;
        
        .detail-item {
          display: flex;
          align-items: center;
          gap: 8px;
          font-size: 14px;
          color: #cbd5e1;
          
          i {
            font-size: 15px;
            color: #93c5fd;
          }
        }
      }
      
      .expert-specialization {
        font-size: 13px;
        margin-bottom: 8px;
        color: #cbd5e1;
        line-height: 1.6;
      }
      
      .expert-languages {
        font-size: 13px;
        margin-bottom: 8px;
        color: #cbd5e1;
        
        .language-tag {
          background: #1e293b;
          border: 1px solid rgba(148, 163, 184, 0.35);
          color: #e2e8f0;
          padding: 2px 8px;
          border-radius: 12px;
          margin-left: 8px;
          font-size: 12px;
        }
      }
      
      .expert-availability {
        font-size: 13px;
        color: #cbd5e1;
      }
    }
  }
}

// ??????????
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  gap: 16px;
  background: #ffffff;
  border: 1px solid #d7dee8;
  border-radius: 14px;
  padding: 14px 16px;
  
  .header-left {
    .logout-button {
      background: #fff;
      border: 1px solid #d5deea;
      border-radius: 10px;
      color: #334155;
      font-weight: 700;
      font-size: 13px;
      padding: 8px 14px;
      
      &:hover {
        border-color: #93c5fd;
        color: #0f172a;
      }
      
      i {
        margin-right: 4px;
        font-size: 14px;
      }
    }
  }
  
  .header-center {
    text-align: center;
    flex: 1;
    
    h2 {
      margin: 0 0 4px 0;
      font-size: 26px;
      font-weight: 800;
      color: var(--text);
      letter-spacing: 0.2px;
    }
    
    .welcome-text {
      font-size: 13px;
      color: #64748b;
      font-weight: 600;
    }
  }
  
  .header-right {
    min-width: 320px;
  }

  :deep(.function-tabs) {
    display: flex;
    gap: 8px;
  }

  :deep(.function-tabs .el-button) {
    height: 34px;
    border-radius: 9px;
    border: 1px solid #d5deea;
    color: #475569;
    background: #ffffff;
    font-size: 12px;
    font-weight: 700;
    padding: 0 12px;
  }

  :deep(.function-tabs .el-button--primary) {
    background: #0f172a;
    border-color: #0f172a;
    color: #f8fafc;
  }
}

// ?????????
.stats-section {
  margin-bottom: 22px;
  
  .stats-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
    gap: 14px;
  }
}

.stat-card {
  background: #ffffff;
  border-radius: 14px;
  padding: 16px;
  border: 1px solid #dbe2ea;
  transition: transform 0.2s ease, box-shadow 0.2s ease, border-color 0.2s ease;
  
  &:hover {
    box-shadow: 0 10px 22px rgba(15, 23, 42, 0.08);
    transform: translateY(-2px);
    border-color: #c4d0e1;
  }
  
  .stat-content {
    display: flex;
    align-items: center;
    gap: 16px;
    
    .stat-icon {
      width: 46px;
      height: 46px;
      border-radius: 10px;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 18px;
      color: white;

      :deep(svg) {
        width: 18px;
        height: 18px;
      }
      
      &.patient {
        background: #4f46e5;
      }
      
      &.consultation {
        background: #db2777;
      }
      
      &.rating {
        background: #0284c7;
      }
      
      &.income {
        background: #16a34a;
      }
    }
    
    .stat-text {
      flex: 1;
      
      .stat-value {
        font-size: 30px;
        font-weight: 800;
        color: var(--text);
        margin-bottom: 2px;
      }
      
      .stat-label {
        font-size: 13px;
        color: #64748b;
        font-weight: 700;
      }
    }
  }
}

.main-content {
  .panel {
    background: #ffffff;
    border-radius: 14px;
    border: 1px solid #dbe2ea;
    margin-bottom: 16px;
    overflow: hidden;
  }
  
  .panel-header {
    background: #f8fafc;
    padding: 14px 16px;
    border-bottom: 1px solid #e2e8f0;
    display: flex;
    justify-content: space-between;
    align-items: center;
    
    h3 {
      margin: 0;
      font-size: 17px;
      font-weight: 800;
      color: var(--text);
      display: flex;
      align-items: center;
      gap: 8px;
      
      &::before {
        content: '';
        width: 3px;
        height: 16px;
        background: #0f172a;
        border-radius: 2px;
      }
    }
    
    .el-button {
      background: #0f172a;
      border: 1px solid #0f172a;
      border-radius: 9px;
      color: #f8fafc;
      font-size: 12px;
      font-weight: 700;
      
      &:hover {
        background: #1e293b;
        border-color: #1e293b;
      }
    }
  }
  
  .panel-content {
    padding: 0;
  }
}

// Element Plus?????/????????????????????
:deep(.el-table) {
  --el-table-border-color: #dde3ec;
  --el-table-header-bg-color: #f1f5f9;
  --el-table-row-hover-bg-color: #f8fafc;
  border-radius: 12px;
  font-size: 13px;
}

:deep(.el-table th) {
  color: #334155;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.3px;
  text-transform: uppercase;
}

:deep(.el-table td) {
  color: #1e293b;
  font-size: 13px;
}

:deep(.el-tag) {
  border-radius: 999px;
  border: 1px solid transparent;
  font-weight: 700;
  letter-spacing: 0.2px;
}

:deep(.el-button) {
  border-radius: 9px;
  font-size: 12px;
  font-weight: 700;
}

:deep(.el-input__wrapper),
:deep(.el-textarea__inner) {
  border-radius: var(--radius-md);
}

:deep(.el-input__wrapper.is-focus),
:deep(.el-textarea__inner:focus) {
  box-shadow: var(--ring) !important;
}

.appointment-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
  
  &:last-child {
    border-bottom: none;
  }
  
  .appointment-info {
    .patient-name {
      font-weight: bold;
      color: #333;
      margin-bottom: 4px;
    }
    
    .appointment-time {
      font-size: 14px;
      color: #666;
    }
  }
}

.consultation-records {
  .panel-content {
    padding: 0;
  }
}

// ??????????
.appointment-item, .consultation-item {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 24px;
  border-bottom: 1px solid rgba(135, 206, 235, 0.1);
  transition: all 0.4s cubic-bezier(0.25, 0.46, 0.45, 0.94);
  background: white;
  
  &:hover {
    background: #f8fafc;
    transform: translateX(6px);
    box-shadow: 0 4px 20px rgba(135, 206, 235, 0.1);
  }
  
  &:last-child {
    border-bottom: none;
  }
  
  .item-info {
    flex: 1;
    
    .item-header {
      display: flex;
      align-items: center;
      margin-bottom: 16px;
      gap: 16px;
      
      .patient-name {
        font-size: 18px;
        font-weight: 600;
        color: #2c3e50;
        letter-spacing: 0.3px;
      }
      
      .el-tag {
        border: none;
        border-radius: 16px;
        font-weight: 500;
        font-size: 13px;
        padding: 6px 16px;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
        
        &.el-tag--success {
          background: #16a34a;
          color: white;
        }
        
        &.el-tag--primary {
          background: #0284c7;
          color: white;
        }
        
        &.el-tag--warning {
          background: #d97706;
          color: white;
        }
      }
    }
    
    .item-details {
      margin-bottom: 16px;
      
      .item-title {
        font-size: 16px;
        font-weight: 500;
        color: #5f9ea0;
        margin-bottom: 8px;
        line-height: 1.4;
      }
      
      .item-time {
        font-size: 14px;
        color: #7f8c8d;
        display: flex;
        align-items: center;
        gap: 8px;
        
        i {
          color: #87ceeb;
        }
      }
    }
    
    .item-description {
      font-size: 15px;
      color: #546e7a;
      line-height: 1.6;
      background: #f8fafc;
      padding: 16px 20px;
      border-radius: 12px;
      border-left: 4px solid #87ceeb;
      box-shadow: 0 2px 12px rgba(135, 206, 235, 0.1);
    }
  }
  
  .item-actions {
    display: flex;
    flex-direction: column;
    gap: 12px;
    margin-left: 20px;
    
    .el-button {
      min-width: 120px;
      border-radius: 12px;
      font-weight: 500;
      font-size: 14px;
      padding: 10px 16px;
      transition: all 0.3s ease;
      border: none;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
      
      &.el-button--primary {
        background: #0284c7;
        
        &:hover {
          transform: translateY(-2px);
          box-shadow: 0 6px 20px rgba(135, 206, 235, 0.4);
        }
      }
      
      &.el-button--success {
        background: #16a34a;
        
        &:hover {
          transform: translateY(-2px);
          box-shadow: 0 6px 20px rgba(152, 251, 152, 0.4);
        }
      }
      
      &.el-button--danger {
        background: #ef4444;
        
        &:hover {
          transform: translateY(-2px);
          box-shadow: 0 6px 20px rgba(255, 107, 107, 0.4);
        }
      }
    }
  }
}

// ??????????
.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #b0c4de;
  
  i {
    font-size: 64px;
    margin-bottom: 20px;
    display: block;
    opacity: 0.6;
  }
  
  p {
    font-size: 16px;
    margin: 0;
    font-weight: 500;
    letter-spacing: 0.5px;
  }
}

:deep(.el-dialog) {
  border-radius: 14px;
  overflow: hidden;
  border: 1px solid #dbe2ea;
  box-shadow: 0 24px 40px rgba(15, 23, 42, 0.14);

  .el-dialog__header {
    margin-right: 0;
    padding: 14px 16px;
    background: #f8fafc;
    border-bottom: 1px solid #e2e8f0;
  }

  .el-dialog__title {
    font-size: 15px;
    font-weight: 800;
    color: #0f172a;
    letter-spacing: 0.2px;
  }

  .el-dialog__body {
    padding: 16px;
    color: #334155;
  }

  .el-dialog__footer {
    border-top: 1px solid #eef2f7;
    padding: 12px 16px 14px;
    display: flex;
    justify-content: flex-end;
    gap: 10px;
  }

  .el-dialog__headerbtn {
    top: 15px;
    right: 14px;
  }

  .el-dialog__headerbtn .el-dialog__close {
    color: #64748b;
    font-size: 16px;
  }
}

:deep(.el-dialog__body) {
  .el-form-item {
    margin-bottom: 18px;
  }

  .el-form-item__label {
    color: #334155;
    font-size: 13px;
    font-weight: 700;
    line-height: 34px;
    padding-right: 12px;
  }

  .el-input__wrapper,
  .el-textarea__inner,
  .el-select__wrapper {
    min-height: 34px;
    border-radius: 10px;
    border: 1px solid #d8e0ea;
    box-shadow: none;
    font-size: 13px;
  }

  .el-textarea__inner {
    line-height: 1.7;
    padding: 10px 12px;
  }
}

:deep(.el-descriptions) {
  border-radius: 10px;
  overflow: hidden;
  border: 1px solid #dbe2ea;
}

:deep(.el-descriptions__table) {
  font-size: 13px;
}

:deep(.el-descriptions__label) {
  width: 120px;
  background: #f1f5f9 !important;
  color: #334155;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.2px;
}

:deep(.el-descriptions__content) {
  color: #1e293b;
  font-size: 13px;
  line-height: 1.7;
}

.consultation-detail {
  :deep(.el-rate) {
    --el-rate-icon-size: 14px;
  }
}

// ???????????
.chat-dialog {
  .el-dialog {
    border-radius: 14px;
    border: 1px solid #dbe2ea;
    box-shadow: 0 20px 36px rgba(15, 23, 42, 0.18);
    
    .el-dialog__header {
      background: #f8fafc;
      padding: 14px 16px;
      border-bottom: 1px solid #e2e8f0;
      margin: 0;
      
      .el-dialog__title {
        font-size: 15px;
        font-weight: 800;
        color: #0f172a;
      }
      
      .el-dialog__headerbtn {
        top: 15px;
        right: 14px;
        
        .el-dialog__close {
          color: #64748b;
          font-size: 16px;
          
          &:hover {
            color: #334155;
          }
        }
      }
    }
    
    .el-dialog__body {
      padding: 0;
    }
  }
}

.chat-container {
  height: 500px;
  display: flex;
  flex-direction: column;
  
  .chat-messages {
    flex: 1;
    padding: 24px;
    overflow-y: auto;
    background: #f8fafc;
    border-bottom: 1px solid #e2e8f0;
    
    .empty-chat {
      text-align: center;
      padding: 80px 20px;
      color: #a0aec0;
      
      i {
        font-size: 64px;
        margin-bottom: 20px;
        display: block;
      }
      
      p {
        font-size: 16px;
        margin: 0;
      }
    }
    
    .message-item {
      display: flex;
      margin-bottom: 20px;
      align-items: flex-start;
      
      &.message-left {
        justify-content: flex-start;
        
        .message-content {
          margin-left: 16px;
          
          .message-bubble {
            background: white;
            border: 1px solid #e2e8f0;
            border-radius: 14px 14px 14px 4px;
            box-shadow: 0 4px 12px rgba(15, 23, 42, 0.06);
          }
        }
      }
      
      &.message-right {
        justify-content: flex-end;
        
        .message-content {
          margin-right: 16px;
          align-items: flex-end;
          
          .message-bubble {
            background: #0f172a;
            color: #f8fafc;
            border-radius: 14px 14px 4px 14px;
            box-shadow: 0 4px 12px rgba(15, 23, 42, 0.2);
          }
        }
      }
      
      .message-avatar {
        .el-avatar {
          border: 2px solid #ffffff;
          box-shadow: 0 2px 8px rgba(15, 23, 42, 0.12);
        }
      }
      
      .message-content {
        display: flex;
        flex-direction: column;
        max-width: 70%;
        
        .message-header {
          margin-bottom: 6px;
          
          .sender-name {
            font-size: 13px;
            color: #64748b;
            font-weight: 700;
          }
        }
        
        .message-bubble {
          padding: 16px 20px;
          
          .message-text {
            font-size: 14px;
            line-height: 1.7;
            word-wrap: break-word;
          }
        }
        
        .message-time {
          font-size: 12px;
          color: #a0aec0;
          margin-top: 6px;
          text-align: right;
        }
      }
    }
  }
  
  .chat-input-area {
    padding: 24px;
    background: white;
    
    .message-input {
      margin-bottom: 16px;
      
      .el-textarea__inner {
        border: 1px solid #d8e0ea;
        border-radius: 10px;
        font-size: 13px;
        line-height: 1.7;
        padding: 10px 12px;
        resize: none;
        
        &:focus {
          border-color: #1d4ed8;
          box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.14);
        }
        
        &::placeholder {
          color: #94a3b8;
        }
      }
    }
    
    .chat-actions {
      display: flex;
      justify-content: flex-end;
      gap: 12px;
      
      .el-button {
        background: #0f172a;
        border: 1px solid #0f172a;
        border-radius: 9px;
        padding: 0 14px;
        min-height: 36px;
        font-weight: 700;
        font-size: 13px;
        
        &:hover {
          background: #1e293b;
          border-color: #1e293b;
        }
      }
    }
  }
}

// ??????????
.dialog-footer {
  padding: 0;
  background: transparent;
  border-top: none;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  
  .el-button {
    border-radius: 9px;
    padding: 0 14px;
    min-height: 34px;
    font-weight: 700;
    font-size: 13px;
    
    &.el-button--default {
      background: #ffffff;
      color: #334155;
      border: 1px solid #d5deea;
      
      &:hover {
        border-color: #b7c4d8;
        color: #0f172a;
      }
    }
    
    &.el-button--success {
      background: #0f172a;
      border: 1px solid #0f172a;
      color: #ffffff;
      
      &:hover {
        background: #1e293b;
        border-color: #1e293b;
      }
    }
  }
}

// ??????????
@media (max-width: 768px) {
  .dashboard-container {
    padding: 16px;
  }
  
  .header-section {
    padding: 24px 20px;
    margin-bottom: 24px;
    
    .expert-header {
      flex-direction: column;
      text-align: center;
      gap: 16px;
      
      .expert-avatar {
        width: 72px;
        height: 72px;
      }
      
      .expert-info {
        h2 {
          font-size: 24px;
        }
        
        .expert-qualifications {
          justify-content: center;
        }
      }
    }
  }
  
  .stats-section .stats-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }
  
  .stat-card {
    padding: 20px;
    
    .stat-content {
      .stat-icon {
        width: 56px;
        height: 56px;
        font-size: 24px;
      }
      
      .stat-text .stat-value {
        font-size: 28px;
      }
    }
  }
  
  .appointment-item, .consultation-item {
    padding: 16px 20px;
    flex-direction: column;
    gap: 16px;
    
    .item-actions {
      margin-left: 0;
      flex-direction: row;
      justify-content: flex-end;
      width: 100%;
      
      .el-button {
        min-width: auto;
        flex: 1;
      }
    }
  }
  
  .chat-dialog .el-dialog {
    width: 95% !important;
    margin: 20px auto;
  }
  
  .chat-container {
    height: 400px;
    
    .chat-messages {
      padding: 16px;
      
      .message-item .message-content {
        max-width: 85%;
      }
    }
    
    .chat-input-area {
      padding: 16px;
    }
  }
}

// ???????????
body {
  font-family: 'Segoe UI', 'Microsoft YaHei', -apple-system, BlinkMacSystemFont, sans-serif;
  line-height: 1.6;
  color: #2c3e50;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

h1, h2, h3, h4, h5, h6 {
  font-weight: 600;
  line-height: 1.3;
  margin-bottom: 0.5em;
  letter-spacing: 0.3px;
}

p {
  margin-bottom: 1em;
  line-height: 1.6;
  letter-spacing: 0.2px;
}

// ????????????
::-webkit-scrollbar {
  width: 8px;
}

::-webkit-scrollbar-track {
  background: rgba(135, 206, 235, 0.1);
  border-radius: 4px;
  margin: 4px 0;
}

::-webkit-scrollbar-thumb {
  background: #0284c7;
  border-radius: 4px;
  border: 2px solid transparent;
  background-clip: padding-box;
  
  &:hover {
    background: #0369a1;
  }
}

// ???????
.el-button {
  transition: all 0.4s cubic-bezier(0.25, 0.46, 0.45, 0.94);
  position: relative;
  overflow: hidden;
  
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: -100%;
    width: 100%;
    height: 100%;
    background: rgba(255, 255, 255, 0.16);
    transition: left 0.6s;
  }
  
  &:hover::before {
    left: 100%;
  }
}

.el-tag {
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

// ???????
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes slideInLeft {
  from {
    opacity: 0;
    transform: translateX(-30px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

@keyframes slideInRight {
  from {
    opacity: 0;
    transform: translateX(30px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

// ???????
@media (max-width: 768px) {
  .dashboard-container {
    padding: 16px;
  }
  
  .header-section {
    padding: 24px 20px;
    
    .expert-header {
      flex-direction: column;
      text-align: center;
      gap: 16px;
      
      .expert-avatar {
        width: 64px;
        height: 64px;
      }
      
      .expert-info h2 {
        font-size: 24px;
      }
    }
  }
  
  .stats-section .stats-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }
  
  .stat-card {
    padding: 20px;
    
    .stat-content {
      .stat-icon {
        width: 48px;
        height: 48px;
        font-size: 20px;
      }
      
      .stat-text .stat-value {
        font-size: 24px;
      }
    }
  }
  
  .page-header .header-center h2 {
    font-size: 24px;
  }
}

// ===== ?1?????? =====

// ?????????
.function-tabs {
  box-shadow: var(--shadow-1);
  border-radius: var(--radius-sm);
  overflow: hidden;
  border: 1px solid var(--border);
  
  .el-button {
    border-radius: 0;
    font-weight: 500;
    padding: 10px 16px;
    border: none;
    background: white;
    color: #4a5568;
    transition: all 0.3s ease;
    
    &:hover {
      background: #f8fafc;
      color: var(--primary);
    }
    
    &.el-button--primary {
      background: var(--primary);
      color: white;
      box-shadow: 0 10px 22px rgba(37, 99, 235, 0.18);
      
      &:hover {
        background: #1d4ed8;
      }
    }
    
    i {
      margin-right: 4px;
      font-size: 14px;
    }
  }
}

// ??????????????????
:deep(.el-button:focus-visible) {
  outline: none;
  box-shadow: var(--ring) !important;
}

// ?????????
.calendar-content {
  .calendar-header {
    text-align: center;
    margin-bottom: 24px;
    
    .current-month {
      font-size: 24px;
      font-weight: 600;
      color: #2d3748;
      margin: 0;
    }
  }
  
  .calendar-grid {
    .calendar-weekdays {
      display: grid;
      grid-template-columns: repeat(7, 1fr);
      gap: 2px;
      margin-bottom: 8px;
      
      .weekday {
        text-align: center;
        font-weight: 600;
        color: #4a5568;
        padding: 12px 0;
        background: #f7fafc;
        border-radius: 8px;
      }
    }
    
    .calendar-days {
      display: grid;
      grid-template-columns: repeat(7, 1fr);
      gap: 8px;
      
      .calendar-day {
        min-height: 100px;
        padding: 8px;
        background: white;
        border: 2px solid #e2e8f0;
        border-radius: 12px;
        cursor: pointer;
        transition: all 0.3s ease;
        position: relative;
        
        &:hover {
          border-color: #667eea;
          transform: translateY(-2px);
          box-shadow: 0 4px 12px rgba(102, 126, 234, 0.15);
        }
        
        &.other-month {
          background: #f7fafc;
          opacity: 0.5;
          
          .day-number {
            color: #a0aec0;
          }
        }
        
        &.today {
          border-color: #667eea;
          background: #eef2ff;
          
          .day-number {
            color: #2563eb;
            font-weight: 700;
          }
        }
        
        &.has-appointment {
          border-color: #48bb78;
        }
        
        .day-number {
          font-size: 16px;
          font-weight: 600;
          color: #2d3748;
          margin-bottom: 8px;
        }
        
        .day-appointments {
          .appointment-dot {
            display: flex;
            align-items: center;
            gap: 4px;
            margin-bottom: 4px;
            padding: 2px 6px;
            background: rgba(255, 255, 255, 0.8);
            border-radius: 4px;
            font-size: 11px;
            
            .dot-badge {
              width: 6px;
              height: 6px;
              border-radius: 50%;
              display: inline-block;
            }
            
            .apt-time {
              color: #4a5568;
              font-size: 11px;
            }
          }
          
          .more-appointments {
            font-size: 11px;
            color: #667eea;
            text-align: center;
            margin-top: 4px;
          }
        }
      }
    }
  }
  
  .schedule-item {
    .schedule-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 12px;
      
      h4 {
        margin: 0;
        font-size: 16px;
        color: #2d3748;
      }
    }
    
    .schedule-info {
      p {
        margin: 8px 0;
        color: #4a5568;
        line-height: 1.6;
      }
    }
  }
}

// ??????????
.statistics-content {
  .chart-panel {
    .chart-container {
      padding: 20px;
    }
  }
  
  .rating-summary {
    display: flex;
    align-items: center;
    gap: 12px;
  }
}

// ?????????
.notes-content {
  .notes-patient-list {
    padding: 0;
    max-height: calc(100vh - 400px);
    overflow-y: auto;
    border-right: 1px solid #e2e8f0;
    
    .patient-item {
      display: flex;
      align-items: center;
      gap: 12px;
      padding: 14px 16px;
      border-bottom: 1px solid #e2e8f0;
      cursor: pointer;
      transition: background-color 0.2s ease, border-color 0.2s ease;
      
      &:hover {
        background: #f8fafc;
      }
      
      &.active {
        background: #eff6ff;
        border-left: 3px solid #1d4ed8;
      }
      
      .patient-avatar {
        flex-shrink: 0;
      }
      
      .patient-basic-info {
        flex: 1;
        
        .patient-name {
          font-weight: 700;
          font-size: 14px;
          color: #0f172a;
          margin-bottom: 3px;
        }
        
        .patient-meta {
          font-size: 12px;
          color: #64748b;
          font-weight: 600;
        }
      }
      
      .note-count {
        flex-shrink: 0;
        
        i {
          font-size: 18px;
          color: #94a3b8;
        }
      }
    }
  }
  
  .notes-list {
    padding: 16px;
    max-height: calc(100vh - 400px);
    overflow-y: auto;
    
    .note-items {
      .note-item {
        background: #ffffff;
        border: 1px solid #dbe2ea;
        border-radius: 12px;
        padding: 14px;
        margin-bottom: 12px;
        transition: transform 0.2s ease, box-shadow 0.2s ease, border-color 0.2s ease;
        
        &:hover {
          box-shadow: 0 8px 18px rgba(15, 23, 42, 0.08);
          transform: translateY(-1px);
          border-color: #c7d2e4;
        }
        
        .note-header {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-bottom: 10px;
          
          .note-date {
            display: flex;
            align-items: center;
            gap: 6px;
            color: #64748b;
            font-size: 12px;
            font-weight: 600;
            
            i {
              color: #94a3b8;
            }
          }
        }
        
        .note-title {
          font-size: 16px;
          font-weight: 800;
          color: #0f172a;
          margin-bottom: 8px;
          letter-spacing: 0.2px;
        }
        
        .note-content {
          color: #334155;
          line-height: 1.75;
          margin-bottom: 10px;
          font-size: 13px;
          white-space: pre-wrap;
        }
        
        .note-tags {
          display: flex;
          flex-wrap: wrap;
          gap: 6px;
        }
      }
    }
  }
  
  .empty-selection {
    min-height: 320px;
    display: flex;
    align-items: center;
    justify-content: center;
  }
}

.notes-content :deep(.el-tag) {
  border-radius: 999px;
  border: 1px solid #cbd5e1;
  background: #f8fafc;
  color: #334155;
  font-size: 12px;
  font-weight: 700;
  padding: 2px 10px;
}

.notes-content :deep(.el-dropdown .el-button) {
  height: 30px;
  border-radius: 8px;
  border: 1px solid #d5deea;
  background: #ffffff;
  color: #475569;
  font-size: 12px;
  font-weight: 700;
}

.notes-content :deep(.el-empty__description p) {
  color: #64748b;
  font-size: 13px;
  font-weight: 600;
}

.notes-content :deep(.el-input__wrapper) {
  min-height: 34px;
  border-radius: 10px;
  border: 1px solid #d8e0ea;
  box-shadow: none;
}

.notes-content :deep(.el-input__inner) {
  font-size: 13px;
  color: #0f172a;
}
</style>
