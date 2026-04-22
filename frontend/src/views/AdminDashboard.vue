<template>
  <div class="dashboard-container">
    <!-- 概览区域 -->
    <div class="overview-section">
      <div class="hero-header">
        <div class="hero-title-wrap">
          <span class="hero-kicker">ADMIN CONSOLE</span>
          <h2 class="section-title">管理后台总览</h2>
          <p class="section-subtitle">统一管理用户、专家、测评、咨询、漂流瓶与 AI 对话记录</p>
        </div>
        <div class="hero-meta">
          <div class="hero-status">
            <span class="status-dot"></span>
            系统运行正常
          </div>
          <div class="dashboard-actions">
            <el-button type="warning" plain @click="goLogin">退出登录</el-button>
          </div>
        </div>
      </div>
      <div class="hero-metrics">
        <div class="hero-metric-item">
          <span class="metric-key">活跃用户</span>
          <strong class="metric-value">{{ systemStats.activeUsers || 0 }}</strong>
        </div>
        <div class="hero-metric-item">
          <span class="metric-key">测评总数</span>
          <strong class="metric-value">{{ systemStats.totalAssessments || 0 }}</strong>
        </div>
        <div class="hero-metric-item">
          <span class="metric-key">今日新增咨询</span>
          <strong class="metric-value">{{ systemStats.todayNewConsultations || 0 }}</strong>
        </div>
      </div>
      <el-row :gutter="20">
        <el-col :span="6">
          <div class="overview-card">
            <div class="overview-head">
              <div class="overview-icon user">
                <i class="el-icon-user"></i>
              </div>
              <div class="overview-label">用户总数</div>
            </div>
            <div class="overview-content">
              <div class="overview-value">{{ systemStats.totalUsers }}</div>
              <div class="overview-change">+{{ systemStats.todayNewUsers || 0 }} 今日新增</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="overview-card">
            <div class="overview-head">
              <div class="overview-icon expert">
                <i class="el-icon-suitcase"></i>
              </div>
              <div class="overview-label">专家总数</div>
            </div>
            <div class="overview-content">
              <div class="overview-value">{{ systemStats.totalPsychologists }}</div>
              <div class="overview-change">+{{ systemStats.todayNewPsychologists || 0 }} 今日新增</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="overview-card">
            <div class="overview-head">
              <div class="overview-icon assessment">
                <i class="el-icon-document"></i>
              </div>
              <div class="overview-label">测评统计</div>
            </div>
            <div class="overview-content">
              <div class="overview-value">{{ systemStats.totalAssessmentTests || systemStats.totalAssessments }}</div>
              <div class="overview-change">共{{ systemStats.totalAssessments }}次测评</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="overview-card">
            <div class="overview-head">
              <div class="overview-icon consultation">
                <i class="el-icon-chat-dot-round"></i>
              </div>
              <div class="overview-label">咨询总数</div>
            </div>
            <div class="overview-content">
              <div class="overview-value">{{ systemStats.totalConsultations }}</div>
              <div class="overview-change">+{{ systemStats.todayNewConsultations || 0 }} 今日新增</div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 管理区域 -->
    <div class="management-section">
      <el-tabs v-model="activeTab" type="card">
        <!-- 用户管理 -->
        <el-tab-pane label="用户管理" name="user">
          <div class="management-panel">
            <div class="panel-header">
              <h3>用户列表</h3>
              <div class="panel-actions">
                <el-input v-model="userSearch" placeholder="搜索用户名/姓名/邮箱" style="width: 200px" clearable />
                <el-button type="success" @click="openAddUser">新增用户</el-button>
                <el-button type="primary" @click="exportUserData">导出数据</el-button>
              </div>
            </div>
            <el-table :data="filteredUsers" style="width: 100%">
              <el-table-column prop="id" label="ID" width="80" />
              <el-table-column prop="username" label="用户名" width="120" />
              <el-table-column prop="realName" label="真实姓名" width="120" />
              <el-table-column prop="email" label="邮箱" width="180" />
              <el-table-column prop="phone" label="手机号" width="120" />
              <el-table-column prop="status" label="状态" width="100">
                <template #default="scope">
                  <el-tag :type="scope.row.status === '正常' ? 'success' : 'danger'">
                    {{ scope.row.status }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="lastLogin" label="最后登录" width="120" />
              <el-table-column label="操作" width="150">
                <template #default="scope">
                  <el-button size="small" @click="editUser(scope.row)">编辑</el-button>
                  <el-button size="small" type="danger" @click="deleteUser(scope.row)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-tab-pane>

        <!-- 专家管理 -->
        <el-tab-pane label="专家管理" name="expert">
          <div class="management-panel">
            <div class="panel-header">
              <h3>专家列表</h3>
              <div class="panel-actions">
                <el-input v-model="expertSearch" placeholder="搜索姓名/职称/领域" style="width: 200px" clearable />
                <el-button type="primary" @click="addExpert">新增专家</el-button>
              </div>
            </div>
            <el-table :data="filteredExperts" style="width: 100%">
              <el-table-column prop="id" label="ID" width="80" />
              <el-table-column prop="realName" label="姓名" width="120" />
              <el-table-column prop="title" label="职称" width="120" />
              <el-table-column prop="specialty" label="擅长领域" width="150" />
              <el-table-column prop="experience" label="从业年限" width="100" />
              <el-table-column prop="rating" label="评分" width="100">
                <template #default="scope">
                  <el-rate v-model="scope.row.rating" disabled size="small" />
                </template>
              </el-table-column>
              <el-table-column prop="status" label="状态" width="100">
                <template #default="scope">
                  <el-tag :type="scope.row.status === '正常' ? 'success' : 'info'">
                    {{ scope.row.status }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="150">
                <template #default="scope">
                  <el-button size="small" @click="editExpert(scope.row)">编辑</el-button>
                  <el-button size="small" type="danger" @click="deleteExpert(scope.row)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-tab-pane>

        <!-- 系统设置 -->
        <el-tab-pane label="系统设置" name="settings">
          <div class="settings-panel">
            <el-form label-width="120px">
              <el-form-item label="平台名称">
                <el-input v-model="systemSettings.platformName" placeholder="请输入平台名称" />
              </el-form-item>
              <el-form-item label="客服热线">
                <el-input v-model="systemSettings.customerService" placeholder="请输入客服电话" />
              </el-form-item>
              <el-form-item label="平台简介">
                <el-input v-model="systemSettings.description" type="textarea" :rows="4" placeholder="请输入平台简介" />
              </el-form-item>
              <el-form-item label="开放用户注册">
                <el-switch v-model="systemSettings.allowRegistration" />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="saveSettings">保存设置</el-button>
              </el-form-item>
            </el-form>
          </div>
        </el-tab-pane>
        <!-- 用户编辑 -->
        <el-dialog v-model="showUserDialog" :title="isUserEdit ? '编辑用户' : '新增用户'" width="520px">
          <el-form ref="userFormRef" :model="userForm" :rules="userRules" label-width="100px">
            <el-form-item label="用户名" prop="username">
              <el-input v-model="userForm.username" />
            </el-form-item>
            <el-form-item label="真实姓名" prop="realName">
              <el-input v-model="userForm.realName" />
            </el-form-item>
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="userForm.email" />
            </el-form-item>
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="userForm.phone" />
            </el-form-item>
            <el-form-item label="状态" prop="status">
              <el-select v-model="userForm.status" style="width: 200px">
                <el-option label="正常" value="正常" />
                <el-option label="禁用" value="禁用" />
              </el-select>
            </el-form-item>
          </el-form>
          <template #footer>
            <el-button @click="showUserDialog=false">取消</el-button>
            <el-button type="primary" @click="submitUser">确认</el-button>
          </template>
        </el-dialog>

        <el-dialog v-model="showExpertDialog" :title="isExpertEdit ? '编辑专家' : '新增专家'" width="560px">
          <el-form ref="expertFormRef" :model="expertForm" :rules="expertRules" label-width="110px">
            <el-form-item label="姓名" prop="realName">
              <el-input v-model="expertForm.realName" />
            </el-form-item>
            <el-form-item label="职称" prop="title">
              <el-input v-model="expertForm.title" />
            </el-form-item>
            <el-form-item label="擅长领域" prop="specialty">
              <el-input v-model="expertForm.specialty" />
            </el-form-item>
            <el-form-item label="从业年限" prop="experienceYears">
              <el-input v-model.number="expertForm.experienceYears" />
            </el-form-item>
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="expertForm.email" />
            </el-form-item>
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="expertForm.phone" />
            </el-form-item>
            <el-form-item label="状态" prop="status">
              <el-select v-model="expertForm.status" style="width: 200px">
                <el-option label="正常" value="正常" />
                <el-option label="禁用" value="禁用" />
              </el-select>
            </el-form-item>
          </el-form>
          <template #footer>
            <el-button @click="showExpertDialog=false">取消</el-button>
            <el-button type="primary" @click="submitExpert">确认</el-button>
          </template>
        </el-dialog>
        <!-- 留言管理 Tab -->
        <el-tab-pane label="留言管理" name="contact">
          <div class="management-panel">
            <div class="panel-header">
              <h3>联系留言列表</h3>
              <div class="panel-actions">
                <el-input v-model="contactSearch" placeholder="搜索主题/姓名/邮箱" style="width: 240px" clearable />
                <el-button type="primary" :loading="contactLoading" @click="loadContactMessages">刷新</el-button>
              </div>
            </div>
            
            <el-table :data="filteredContactMessages" v-loading="contactLoading" border>
              <el-table-column prop="id" label="ID" width="80" />
              <el-table-column prop="name" label="姓名" width="140" />
              <el-table-column prop="email" label="邮箱" width="200" />
              <el-table-column prop="phone" label="手机号" width="140" />
              <el-table-column prop="subject" label="主题" min-width="160" />
              <el-table-column prop="content" label="留言内容" min-width="240" />
              <el-table-column label="状态" width="120">
                <template #default="{ row }">
                  <el-tag :type="row.status === 'replied' ? 'success' : 'warning'">
                    {{ row.status === 'replied' ? '已回复' : '待回复' }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="createTime" label="创建时间" width="180" />
              <el-table-column prop="replyContent" label="回复内容" min-width="200" show-overflow-tooltip />
              <el-table-column label="操作" width="140">
                <template #default="{ row }">
                  <el-button type="primary" text @click="openContactReplyDialog(row)">查看/回复</el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
          
          <!-- 留言回复弹窗 -->
          <el-dialog v-model="showReplyDialog" title="回复联系留言" width="600px">
            <div class="reply-dialog-body">
              <el-descriptions :column="1" border>
                <el-descriptions-item label="姓名">{{ currentMessage?.name }}</el-descriptions-item>
                <el-descriptions-item label="邮箱">{{ currentMessage?.email }}</el-descriptions-item>
                <el-descriptions-item label="手机号">{{ currentMessage?.phone }}</el-descriptions-item>
                <el-descriptions-item label="主题">{{ currentMessage?.subject }}</el-descriptions-item>
                <el-descriptions-item label="留言内容">{{ currentMessage?.content }}</el-descriptions-item>
                <el-descriptions-item label="当前状态">
                  <el-tag :type="currentMessage?.status === 'replied' ? 'success' : 'warning'">
                    {{ currentMessage?.status === 'replied' ? '已回复' : '待回复' }}
                  </el-tag>
                </el-descriptions-item>
              </el-descriptions>
              
              <div style="margin-top: 16px;">
                <el-input
                  v-model="replyContent"
                  type="textarea"
                  :rows="5"
                  placeholder="请输入回复内容"
                  maxlength="1000"
                  show-word-limit
                />
              </div>
            </div>
            <template #footer>
              <el-button @click="showReplyDialog=false">取消</el-button>
              <el-button type="primary" :loading="replying" @click="submitContactReply">发送回复</el-button>
            </template>
          </el-dialog>
        </el-tab-pane>

        <!-- 测评管理 -->
        <el-tab-pane label="测评管理" name="assessment">
          <div class="management-panel">
            <div class="panel-header">
              <h3>测评列表管理</h3>
              <div class="panel-actions">
                <el-input v-model="assessmentSearch" placeholder="搜索测评" style="width: 200px" clearable />
                <el-button type="success" @click="openAddAssessment">新增测评</el-button>
              </div>
            </div>
            <el-table :data="filteredAssessments" v-loading="assessmentLoading" border>
              <el-table-column prop="id" label="ID" width="80" />
              <el-table-column prop="title" label="测评标题" min-width="180" />
              <el-table-column prop="category" label="分类" width="120" />
              <el-table-column prop="questionsCount" label="题目数" width="100" />
              <el-table-column prop="estimatedTime" label="预计时长(分钟)" width="140" />
              <el-table-column label="状态" width="100">
                <template #default="{ row }">
                  <el-tag :type="row.status === '正常' ? 'success' : 'info'">{{ row.status }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="createTime" label="创建时间" width="180" />
              <el-table-column label="操作" width="180">
                <template #default="{ row }">
                  <el-button size="small" @click="editAssessment(row)">编辑</el-button>
                  <el-button size="small" type="danger" @click="deleteAssessmentItem(row)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
          
          <el-dialog v-model="showAssessmentDialog" :title="isAssessmentEdit ? '编辑测评' : '新增测评'" width="600px">
            <el-form ref="assessmentFormRef" :model="assessmentForm" :rules="assessmentRules" label-width="120px">
              <el-form-item label="测评标题" prop="title">
                <el-input v-model="assessmentForm.title" placeholder="请输入测评标题" />
              </el-form-item>
              <el-form-item label="描述" prop="description">
                <el-input v-model="assessmentForm.description" type="textarea" :rows="3" placeholder="请输入测评描述" />
              </el-form-item>
              <el-form-item label="分类" prop="category">
                <el-select v-model="assessmentForm.category" placeholder="请选择分类" style="width: 100%">
                  <el-option label="抑郁" value="抑郁" />
                  <el-option label="焦虑" value="焦虑" />
                  <el-option label="压力" value="压力" />
                  <el-option label="人格" value="人格" />
                  <el-option label="睡眠" value="睡眠" />
                </el-select>
              </el-form-item>
              <el-form-item label="题目数量" prop="questionsCount">
                <el-input-number v-model="assessmentForm.questionsCount" :min="1" :max="100" />
              </el-form-item>
              <el-form-item label="预计时长(分钟)" prop="estimatedTime">
                <el-input-number v-model="assessmentForm.estimatedTime" :min="1" :max="120" />
              </el-form-item>
              <el-form-item label="状态" prop="status">
                <el-select v-model="assessmentForm.status" style="width: 100%">
                  <el-option label="正常" value="正常" />
                  <el-option label="禁用" value="禁用" />
                </el-select>
              </el-form-item>
            </el-form>
            <template #footer>
              <el-button @click="showAssessmentDialog=false">取消</el-button>
              <el-button type="primary" @click="submitAssessment">确认</el-button>
            </template>
          </el-dialog>
        </el-tab-pane>

        <!-- 咨询管理 -->
        <el-tab-pane label="咨询管理" name="consultation">
          <div class="management-panel">
            <div class="panel-header">
              <h3>咨询记录列表</h3>
              <div class="panel-actions">
                <el-input v-model="consultationSearch" placeholder="搜索咨询记录" style="width: 200px" clearable />
                <el-button type="primary" :loading="consultationLoading" @click="loadConsultationManagementData">刷新</el-button>
              </div>
            </div>
            <el-table :data="filteredConsultations" v-loading="consultationLoading" border>
              <el-table-column prop="id" label="ID" width="80" />
              <el-table-column prop="userName" label="用户" width="120" />
              <el-table-column prop="psychologistName" label="专家" width="120" />
              <el-table-column prop="title" label="咨询主题" min-width="180" />
              <el-table-column prop="consultationType" label="咨询类型" width="120" />
              <el-table-column label="状态" width="100">
                <template #default="{ row }">
                  <el-tag :type="row.status === '已完成' ? 'success' : row.status === '进行中' ? 'warning' : 'info'">
                    {{ row.status }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="scheduledTime" label="预约时间" width="180" />
              <el-table-column prop="cost" label="费用" width="100" />
              <el-table-column label="操作" width="120">
                <template #default="{ row }">
                  <el-button size="small" type="primary" text @click="viewConsultationDetail(row)">查看</el-button>
                  <el-button size="small" type="danger" @click="deleteConsultationItem(row)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
          
          <el-dialog v-model="showConsultationDetailDialog" title="咨询记录详情" width="700px">
            <el-descriptions :column="2" border v-if="currentConsultation">
              <el-descriptions-item label="咨询ID">{{ currentConsultation.id }}</el-descriptions-item>
              <el-descriptions-item label="用户">{{ currentConsultation.userName }}</el-descriptions-item>
              <el-descriptions-item label="专家">{{ currentConsultation.psychologistName }}</el-descriptions-item>
              <el-descriptions-item label="咨询类型">{{ currentConsultation.consultationType }}</el-descriptions-item>
              <el-descriptions-item label="咨询主题" :span="2">{{ currentConsultation.title }}</el-descriptions-item>
              <el-descriptions-item label="描述" :span="2">{{ currentConsultation.description }}</el-descriptions-item>
              <el-descriptions-item label="状态">
                <el-tag :type="currentConsultation.status === '已完成' ? 'success' : 'warning'">
                  {{ currentConsultation.status }}
                </el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="费用">¥{{ currentConsultation.cost }}</el-descriptions-item>
              <el-descriptions-item label="预约时间" :span="2">{{ currentConsultation.scheduledTime }}</el-descriptions-item>
              <el-descriptions-item label="用户评分">{{ currentConsultation.userRating || '未评分' }}</el-descriptions-item>
              <el-descriptions-item label="用户反馈" :span="2">{{ currentConsultation.userFeedback || '暂无' }}</el-descriptions-item>
            </el-descriptions>
            <template #footer>
              <el-button @click="showConsultationDetailDialog=false">关闭</el-button>
            </template>
          </el-dialog>
        </el-tab-pane>

        <!-- 漂流瓶管理 -->
        <el-tab-pane label="漂流瓶管理" name="bottle">
          <div class="management-panel">
            <div class="panel-header">
              <h3>漂流瓶列表</h3>
              <div class="panel-actions">
                <el-input v-model="bottleSearch" placeholder="搜索漂流瓶" style="width: 200px" clearable />
                <el-button type="primary" :loading="bottleLoading" @click="loadBottleManagementData">刷新</el-button>
              </div>
            </div>
            <el-table :data="filteredBottles" v-loading="bottleLoading" border>
              <el-table-column prop="id" label="ID" width="80" />
              <el-table-column prop="userName" label="发布者" width="120" />
              <el-table-column prop="content" label="内容" min-width="300" show-overflow-tooltip />
              <el-table-column prop="likes" label="点赞数" width="100" />
              <el-table-column prop="replyCount" label="回复数" width="100" />
              <el-table-column prop="createTime" label="发布时间" width="180" />
              <el-table-column label="操作" width="120">
                <template #default="{ row }">
                  <el-button size="small" type="primary" text @click="viewBottleDetail(row)">查看</el-button>
                  <el-button size="small" type="danger" @click="deleteBottleItem(row)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
          
          <el-dialog v-model="showBottleDetailDialog" title="漂流瓶详情" width="600px">
            <div v-if="currentBottle">
              <el-descriptions :column="1" border>
                <el-descriptions-item label="ID">{{ currentBottle.id }}</el-descriptions-item>
                <el-descriptions-item label="发布者">{{ currentBottle.userName }}</el-descriptions-item>
                <el-descriptions-item label="内容">{{ currentBottle.content }}</el-descriptions-item>
                <el-descriptions-item label="点赞数">{{ currentBottle.likes }}</el-descriptions-item>
                <el-descriptions-item label="回复数">{{ currentBottle.replyCount }}</el-descriptions-item>
                <el-descriptions-item label="发布时间">{{ currentBottle.createTime }}</el-descriptions-item>
              </el-descriptions>
            </div>
            <template #footer>
              <el-button @click="showBottleDetailDialog=false">关闭</el-button>
            </template>
          </el-dialog>
        </el-tab-pane>

        <!-- AI 对话记录 -->
        <el-tab-pane label="AI对话记录" name="ai">
          <div class="management-panel">
            <div class="panel-header">
              <h3>AI对话记录列表</h3>
              <div class="panel-actions">
                <el-input v-model="aiSearch" placeholder="搜索对话记录" style="width: 200px" clearable />
                <el-button type="primary" :loading="aiLoading" @click="loadAIManagementData">刷新</el-button>
              </div>
            </div>
            <el-table :data="filteredAIRecords" v-loading="aiLoading" border>
              <el-table-column prop="id" label="ID" width="80" />
              <el-table-column prop="userName" label="用户" width="120" />
              <el-table-column prop="sessionType" label="对话类型" width="120" />
              <el-table-column prop="userMessage" label="用户消息" min-width="250" show-overflow-tooltip />
              <el-table-column prop="aiResponse" label="AI回复" min-width="250" show-overflow-tooltip />
              <el-table-column prop="createTime" label="对话时间" width="180" />
              <el-table-column label="操作" width="120">
                <template #default="{ row }">
                  <el-button size="small" type="primary" text @click="viewAIDetail(row)">查看</el-button>
                  <el-button size="small" type="danger" @click="deleteAIItem(row)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
          
          <el-dialog v-model="showAIDetailDialog" title="AI对话详情" width="700px">
            <div v-if="currentAIRecord">
              <el-descriptions :column="1" border>
                <el-descriptions-item label="对话ID">{{ currentAIRecord.id }}</el-descriptions-item>
                <el-descriptions-item label="用户">{{ currentAIRecord.userName }}</el-descriptions-item>
                <el-descriptions-item label="对话类型">{{ currentAIRecord.sessionType }}</el-descriptions-item>
                <el-descriptions-item label="对话时间">{{ currentAIRecord.createTime }}</el-descriptions-item>
              </el-descriptions>
              <div style="margin-top: 20px;">
                <div style="background: #f0f2f5; padding: 12px; border-radius: 8px; margin-bottom: 12px;">
                  <strong>用户消息：</strong>
                  <p style="margin: 8px 0 0 0;">{{ currentAIRecord.userMessage }}</p>
                </div>
                <div style="background: #e6f7ff; padding: 12px; border-radius: 8px;">
                  <strong>AI回复：</strong>
                  <p style="margin: 8px 0 0 0;">{{ currentAIRecord.aiResponse }}</p>
                </div>
              </div>
            </div>
            <template #footer>
              <el-button @click="showAIDetailDialog=false">关闭</el-button>
            </template>
          </el-dialog>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  getSystemStats, 
  getUserManagementData, 
  getPsychologistManagementData, 
  createUser, 
  updateUser, 
  deleteUser as apiDeleteUser, 
  createPsychologist, 
  updatePsychologist, 
  deletePsychologist as apiDeletePsychologist,
  getAssessmentManagementData,
  createAssessment,
  updateAssessment,
  deleteAssessment,
  getConsultationManagementData,
  deleteConsultation,
  getBottleManagementData,
  deleteBottle,
  getAIManagementData,
  deleteAIRecord
} from '@/api/admin'
import { useRouter } from 'vue-router'
const router = useRouter()
const goLogin = () => {
  router.push('/login')
}

const activeTab = ref('user')
const userSearch = ref('')
const expertSearch = ref('')
const loading = ref(false)

const systemStats = ref({
  totalUsers: 0,
  totalPsychologists: 0,
  totalAssessments: 0,
  totalConsultations: 0,
  activeUsers: 0
})

const userList = ref([])
const expertList = ref([])

// ?????????????????????????????????????
const filteredUsers = computed(() => {
  const keyword = userSearch.value.trim().toLowerCase()
  return userList.value.filter(u => {
    const username = (u.username || '').toLowerCase()
    const realName = (u.realName || '').toLowerCase()
    const email = (u.email || '').toLowerCase()
    const phone = (u.phone || '').toLowerCase()
    if (!keyword) return true
    return username.includes(keyword) || realName.includes(keyword) || email.includes(keyword) || phone.includes(keyword)
  })
})

const filteredExperts = computed(() => {
  const keyword = expertSearch.value.trim().toLowerCase()
  return expertList.value.filter(e => {
    const realName = (e.realName || '').toLowerCase()
    const title = (e.title || '').toLowerCase()
    const specialty = (e.specialty || '').toLowerCase()
    const email = (e.email || '').toLowerCase()
    const phone = (e.phone || '').toLowerCase()
    if (!keyword) return true
    return realName.includes(keyword) || title.includes(keyword) || specialty.includes(keyword) || email.includes(keyword) || phone.includes(keyword)
  })
})

// ????????
const showUserDialog = ref(false)
const isUserEdit = ref(false)
const userForm = ref({ id: null, username: '', realName: '', email: '', phone: '', status: '正常' })
const userFormRef = ref(null)

const showExpertDialog = ref(false)
const isExpertEdit = ref(false)
const expertForm = ref({ id: null, realName: '', title: '', specialty: '', experienceYears: null, email: '', phone: '', status: '正常' })
const expertFormRef = ref(null)

const systemSettings = ref({
  platformName: '星语航海心理测评与咨询平台',
  customerService: '400-123-4567',
  description: '为用户提供心理测评、专家咨询、情绪疏导与 AI 辅助支持服务',
  allowRegistration: true
})
const validateEmail = (rule, value, callback) => {
  if (!value) return callback()
  const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  re.test(value) ? callback() : callback(new Error('请输入正确的邮箱地址'))
}
const validatePhone = (rule, value, callback) => {
  if (!value) return callback()
  const re = /^1[3-9]\d{9}$/
  re.test(value) ? callback() : callback(new Error('请输入正确的手机号'))
}
const validateNonNegInt = (rule, value, callback) => {
  if (value === null || value === undefined || value === '') return callback(new Error('请输入从业年限'))
  if (!Number.isInteger(value) || value < 0) return callback(new Error('从业年限必须为非负整数'))
  callback()
}

const userRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度3-20个字符', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9_]+$/, message: '仅限字母、数字、下划线', trigger: 'blur' }
  ],
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' },
    { min: 2, message: '至少2个字符', trigger: 'blur' }
  ],
  email: [{ validator: validateEmail, trigger: 'blur' }],
  phone: [{ validator: validatePhone, trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

const expertRules = {
  realName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  title: [{ required: true, message: '请输入职称', trigger: 'blur' }],
  specialty: [{ required: true, message: '请输入擅长领域', trigger: 'blur' }],
  experienceYears: [{ validator: validateNonNegInt, trigger: ['blur', 'change'] }],
  email: [{ validator: validateEmail, trigger: 'blur' }],
  phone: [{ validator: validatePhone, trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

const exportUserData = () => {
  // ??????????????????
}

// ???????/??/??
const openAddUser = () => {
  isUserEdit.value = false
  userForm.value = { id: null, username: '', realName: '', email: '', phone: '', status: '正常' }
  showUserDialog.value = true
}

const submitUser = async () => {
  const valid = await userFormRef.value.validate()
  if (!valid) {
    ElMessage.warning('请完善表单后再提交')
    return
  }
  try {
    const resp = isUserEdit.value ? await updateUser(userForm.value) : await createUser(userForm.value)
    if (resp.success) {
      // ?????????
      showUserDialog.value = false
      await loadUserManagementData()
    } else {
      ElMessage.error(resp.message || '提交失败')
    }
  } catch (e) {
    ElMessage.error('操作失败: ' + e.message)
  }
}

// ???????/??/??
const addExpert = () => {
  isExpertEdit.value = false
  expertForm.value = { id: null, realName: '', title: '', specialty: '', experienceYears: null, email: '', phone: '', status: '正常' }
  showExpertDialog.value = true
}

const submitExpert = async () => {
  const valid = await expertFormRef.value.validate()
  if (!valid) {
    ElMessage.warning('请完善表单后再提交')
    return
  }
  try {
    const resp = isExpertEdit.value ? await updatePsychologist(expertForm.value) : await createPsychologist(expertForm.value)
    if (resp.success) {
      // ?????????
      showExpertDialog.value = false
      await loadPsychologistManagementData()
    } else {
      ElMessage.error(resp.message || '提交失败')
    }
  } catch (e) {
    ElMessage.error('操作失败: ' + e.message)
  }
}

const editUser = (user) => {
  isUserEdit.value = true
  userForm.value = { id: user.id, username: user.username, realName: user.realName, email: user.email, phone: user.phone, status: user.status }
  showUserDialog.value = true
}

const deleteUser = async (user) => {
  try {
    await ElMessageBox.confirm(`确定删除用户 ${user.realName} 吗？`, '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const resp = await apiDeleteUser(user.id)
    if (resp.success) {
      // ?????????
      await loadUserManagementData()
    } else {
      ElMessage.error(resp.message || '删除失败')
    }
  } catch {
    // ??????????
  }
}

const editExpert = (expert) => {
  isExpertEdit.value = true
  expertForm.value = { id: expert.id, realName: expert.realName, title: expert.title, specialty: expert.specialty, experienceYears: parseInt(expert.experience) || null, email: expert.email || '', phone: expert.phone || '', status: expert.status === '正常' ? '正常' : '禁用' }
  showExpertDialog.value = true
}

const deleteExpert = async (expert) => {
  try {
    await ElMessageBox.confirm(`确定删除专家 ${expert.realName} 吗？`, '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const resp = await apiDeletePsychologist(expert.id)
    if (resp.success) {
      // ?????????
      await loadPsychologistManagementData()
    } else {
      ElMessage.error(resp.message || '删除失败')
    }
  } catch {
    // ??????????
  }
}

const saveSettings = () => {
  // ???????????
}

onMounted(() => {
  loadSystemStats()
  loadUserManagementData()
  loadPsychologistManagementData()
})

// ?????????????
const loadSystemStats = async () => {
  try {
    loading.value = true
    const response = await getSystemStats()
    if (response.success) {
      systemStats.value = response
    } else {
      ElMessage.error('获取系统统计数据失败: ' + response.message)
    }
  } catch (error) {
    ElMessage.error('获取系统统计数据失败: ' + error.message)
  } finally {
    loading.value = false
  }
}

// ???????????????
const loadUserManagementData = async () => {
  try {
    const response = await getUserManagementData()
    if (response.success) {
      userList.value = response.users.map(user => ({
        id: user.id,
        username: user.username,
        realName: user.realName,
        email: user.email,
        phone: user.phone,
        // ??????????????? 1/0 ?? "????"/"????"????????????
        status: (user.status === 1 || user.status === '1' || (typeof user.status === 'string' && user.status.includes('正'))) ? '正常' : '禁用',
        lastLogin: user.lastLoginTime ? new Date(user.lastLoginTime).toLocaleDateString() : '从未登录'
      }))
    } else {
      ElMessage.error('获取用户数据失败: ' + response.message)
    }
  } catch (error) {
    ElMessage.error('获取用户数据失败: ' + error.message)
  }
}

// ??????????????
const loadPsychologistManagementData = async () => {
  try {
    const response = await getPsychologistManagementData()
    if (response.success) {
      expertList.value = response.psychologists.map(psychologist => ({
        id: psychologist.id,
        realName: psychologist.realName,
        title: psychologist.title,
        specialty: psychologist.specialty,
        experience: psychologist.experienceYears ? `${psychologist.experienceYears}年` : '-',
        rating: psychologist.rating ? psychologist.rating : 4.5,
        // ???????????????????????/??????????? 1/0 ?? ????
        status: (psychologist.status === 1 || psychologist.status === '1' || psychologist.status === '正常' || (typeof psychologist.status === 'string' && psychologist.status.includes('正'))) ? '正常' : '禁用'
      }))
    } else {
      ElMessage.error('获取专家数据失败: ' + response.message)
    }
  } catch (error) {
    ElMessage.error('获取专家数据失败: ' + error.message)
  }
}
import { getAllContactMessages, replyContactMessage } from '@/api/contact'

// ????????????????
const contactMessageList = ref([])
const contactSearch = ref('')
const contactLoading = ref(false)
const showReplyDialog = ref(false)
const currentMessage = ref(null)
const replyContent = ref('')
const replying = ref(false)

const loadContactMessages = async () => {
  contactLoading.value = true
  try {
    const res = await getAllContactMessages()
    if (res.code === 200) {
      contactMessageList.value = res.messages || []
    } else {
      ElMessage.error(res.message || '加载留言失败')
    }
  } catch (e) {
    ElMessage.error('加载留言失败')
  } finally {
    contactLoading.value = false
  }
}

const filteredContactMessages = computed(() => {
  const keyword = contactSearch.value.trim().toLowerCase()
  if (!keyword) return contactMessageList.value
  return contactMessageList.value.filter(m => {
    return (
      (m.subject || '').toLowerCase().includes(keyword) ||
      (m.name || '').toLowerCase().includes(keyword) ||
      (m.email || '').toLowerCase().includes(keyword)
    )
  })
})

const openContactReplyDialog = (row) => {
  currentMessage.value = row
  replyContent.value = row.replyContent || ''
  showReplyDialog.value = true
}

const submitContactReply = async () => {
  if (!currentMessage.value) return
  const content = replyContent.value.trim()
  if (!content) {
    ElMessage.warning('请输入回复内容')
    return
  }
  replying.value = true
  try {
    const res = await replyContactMessage({ messageId: currentMessage.value.id, replyContent: content })
    if (res.code === 200) {
      // ?????????
      // ????????
      currentMessage.value.status = 'replied'
      currentMessage.value.replyContent = content
      showReplyDialog.value = false
    } else {
      ElMessage.error(res.message || '回复失败')
    }
  } catch (e) {
    ElMessage.error('回复失败')
  } finally {
    replying.value = false
  }
}

// ??????????
const assessmentList = ref([])
const assessmentSearch = ref('')
const assessmentLoading = ref(false)
const showAssessmentDialog = ref(false)
const isAssessmentEdit = ref(false)
const assessmentForm = ref({ id: null, title: '', description: '', category: '', questionsCount: 10, estimatedTime: 10, status: '正常' })
const assessmentFormRef = ref(null)

const assessmentRules = {
  title: [{ required: true, message: '请输入测评标题', trigger: 'blur' }],
  description: [{ required: true, message: '请输入测评描述', trigger: 'blur' }],
  category: [{ required: true, message: '请选择分类', trigger: 'change' }],
  questionsCount: [{ required: true, message: '请输入题目数量', trigger: 'blur' }],
  estimatedTime: [{ required: true, message: '请输入预计时长', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

const filteredAssessments = computed(() => {
  const keyword = assessmentSearch.value.trim().toLowerCase()
  if (!keyword) return assessmentList.value
  return assessmentList.value.filter(a => {
    return (a.title || '').toLowerCase().includes(keyword) || (a.category || '').toLowerCase().includes(keyword)
  })
})

const loadAssessmentManagementData = async () => {
  assessmentLoading.value = true
  try {
    const res = await getAssessmentManagementData()
    if (res.success) {
      assessmentList.value = (res.assessments || []).map(a => ({
        id: a.id,
        title: a.title,
        description: a.description,
        category: a.category,
        questionsCount: a.questionsCount,
        estimatedTime: a.estimatedTime,
        status: a.status === '正常' || a.status === 1 ? '正常' : '禁用',
        createTime: a.createTime
      }))
    } else {
      ElMessage.error(res.message || '加载测评数据失败')
    }
  } catch (e) {
    ElMessage.error('加载测评数据失败')
  } finally {
    assessmentLoading.value = false
  }
}

const openAddAssessment = () => {
  isAssessmentEdit.value = false
  assessmentForm.value = { id: null, title: '', description: '', category: '', questionsCount: 10, estimatedTime: 10, status: '正常' }
  showAssessmentDialog.value = true
}

const editAssessment = (row) => {
  isAssessmentEdit.value = true
  assessmentForm.value = { ...row }
  showAssessmentDialog.value = true
}

const submitAssessment = async () => {
  const valid = await assessmentFormRef.value.validate()
  if (!valid) {
    ElMessage.warning('请完善表单后再提交')
    return
  }
  try {
    const resp = isAssessmentEdit.value ? await updateAssessment(assessmentForm.value) : await createAssessment(assessmentForm.value)
    if (resp.success) {
      // ?????????
      showAssessmentDialog.value = false
      await loadAssessmentManagementData()
    } else {
      ElMessage.error(resp.message || '提交失败')
    }
  } catch (e) {
    ElMessage.error('操作失败: ' + e.message)
  }
}

const deleteAssessmentItem = async (row) => {
  try {
    await ElMessageBox.confirm(`确定删除测评 "${row.title}" 吗？`, '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const resp = await deleteAssessment(row.id)
    if (resp.success) {

      // ?????????
      await loadAssessmentManagementData()
    } else {
      ElMessage.error(resp.message || '删除失败')
    }
  } catch {
    // ??????????
  }
}

// ????????????
const consultationList = ref([])
const consultationSearch = ref('')
const consultationLoading = ref(false)
const showConsultationDetailDialog = ref(false)
const currentConsultation = ref(null)

const filteredConsultations = computed(() => {
  const keyword = consultationSearch.value.trim().toLowerCase()
  if (!keyword) return consultationList.value
  return consultationList.value.filter(c => {
    return (c.title || '').toLowerCase().includes(keyword) || 
           (c.userName || '').toLowerCase().includes(keyword) ||
           (c.psychologistName || '').toLowerCase().includes(keyword)
  })
})

const loadConsultationManagementData = async () => {
  consultationLoading.value = true
  try {
    const res = await getConsultationManagementData()
    if (res.success) {
      consultationList.value = (res.consultations || []).map(c => ({
        id: c.id,
        userName: c.userName || '未知用户',
        psychologistName: c.psychologistName || '未知专家',
        title: c.title,
        description: c.description,
        consultationType: c.consultationType,
        status: c.status,
        scheduledTime: c.scheduledTime,
        cost: c.cost ? `${c.cost}` : '暂无',
        userRating: c.userRating,
        userFeedback: c.userFeedback
      }))
    } else {
      ElMessage.error(res.message || '加载咨询数据失败')
    }
  } catch (e) {
    ElMessage.error('加载咨询数据失败')
  } finally {
    consultationLoading.value = false
  }
}

const viewConsultationDetail = (row) => {
  currentConsultation.value = row
  showConsultationDetailDialog.value = true
}

const deleteConsultationItem = async (row) => {
  try {
    await ElMessageBox.confirm(`确定删除咨询记录 "${row.title}" 吗？`, '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const resp = await deleteConsultation(row.id)
    if (resp.success) {
      // ?????????
      await loadConsultationManagementData()
    } else {
      ElMessage.error(resp.message || '删除失败')
    }
  } catch {
    // ??????????
  }
}

// ??????????
const bottleList = ref([])
const bottleSearch = ref('')
const bottleLoading = ref(false)
const showBottleDetailDialog = ref(false)
const currentBottle = ref(null)

const filteredBottles = computed(() => {
  const keyword = bottleSearch.value.trim().toLowerCase()
  if (!keyword) return bottleList.value
  return bottleList.value.filter(b => {
    return (b.content || '').toLowerCase().includes(keyword) || (b.userName || '').toLowerCase().includes(keyword)
  })
})

const loadBottleManagementData = async () => {
  bottleLoading.value = true
  try {
    const res = await getBottleManagementData()
    if (res.success) {
      bottleList.value = (res.bottles || []).map(b => ({
        id: b.id,
        userName: b.userName || '未知用户',
        content: b.content,
        likes: b.likes || 0,
        replyCount: b.replyCount || 0,
        createTime: b.createTime
      }))
    } else {
      ElMessage.error(res.message || '加载漂流瓶数据失败')
    }
  } catch (e) {
    ElMessage.error('加载漂流瓶数据失败')
  } finally {
    bottleLoading.value = false
  }
}

const viewBottleDetail = (row) => {
  currentBottle.value = row
  showBottleDetailDialog.value = true
}

const deleteBottleItem = async (row) => {
  try {
    await ElMessageBox.confirm('确定删除这条漂流瓶吗？', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const resp = await deleteBottle(row.id)
    if (resp.success) {
      // ?????????
      await loadBottleManagementData()
    } else {
      ElMessage.error(resp.message || '删除失败')
    }
  } catch {
    // ??????????
  }
}

// AI????????????
const aiRecordList = ref([])
const aiSearch = ref('')
const aiLoading = ref(false)
const showAIDetailDialog = ref(false)
const currentAIRecord = ref(null)

const filteredAIRecords = computed(() => {
  const keyword = aiSearch.value.trim().toLowerCase()
  if (!keyword) return aiRecordList.value
  return aiRecordList.value.filter(ai => {
    return (ai.userMessage || '').toLowerCase().includes(keyword) || 
           (ai.aiResponse || '').toLowerCase().includes(keyword) ||
           (ai.userName || '').toLowerCase().includes(keyword)
  })
})

const loadAIManagementData = async () => {
  aiLoading.value = true
  try {
    const res = await getAIManagementData()
    if (res.success) {
      aiRecordList.value = (res.aiRecords || []).map(ai => ({
        id: ai.id,
        userName: ai.userName || '未知用户',
        sessionType: ai.sessionType || '未知类型',
        userMessage: ai.userMessage,
        aiResponse: ai.aiResponse,
        createTime: ai.createTime
      }))
    } else {
      ElMessage.error(res.message || '加载AI对话记录失败')
    }
  } catch (e) {
    ElMessage.error('加载AI对话记录失败')
  } finally {
    aiLoading.value = false
  }
}

const viewAIDetail = (row) => {
  currentAIRecord.value = row
  showAIDetailDialog.value = true
}

const deleteAIItem = async (row) => {
  try {
    await ElMessageBox.confirm('确定删除这条AI对话记录吗？', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const resp = await deleteAIRecord(row.id)
    if (resp.success) {
      // ?????????
      await loadAIManagementData()
    } else {
      ElMessage.error(resp.message || '删除失败')
    }
  } catch {
    // ??????????
  }
}

onMounted(() => {
  loadContactMessages()
  loadAssessmentManagementData()
  loadConsultationManagementData()
  loadBottleManagementData()
  loadAIManagementData()
})
</script>

<style lang="scss" scoped>
.dashboard-container {
  min-height: 100vh;
  background: #f3f5f8;
  padding: 28px;
  color: #111827;
}

.overview-section {
  margin-bottom: 24px;
  background: #ffffff;
  border: 1px solid #e2e8f0;
  border-radius: 20px;
  padding: 22px;
  box-shadow: 0 10px 28px rgba(15, 23, 42, 0.05);
}

.hero-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
}

.hero-kicker {
  display: inline-block;
  margin-bottom: 8px;
  padding: 3px 8px;
  border-radius: 999px;
  border: 1px solid #cbd5e1;
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 0.6px;
  color: #475569;
}

.hero-title-wrap {
  .section-title {
    margin: 0;
    font-size: 30px;
    font-weight: 800;
    letter-spacing: 0.3px;
    color: #0f172a;
  }

  .section-subtitle {
    margin: 8px 0 0;
    color: #64748b;
    font-size: 14px;
    line-height: 1.6;
  }
}

.dashboard-actions {
  :deep(.el-button) {
    border-radius: 10px;
    padding: 10px 16px;
    font-weight: 600;
    border-color: #d6dae1;
    color: #334155;
    background: #ffffff;
  }
}

.hero-meta {
  display: flex;
  align-items: center;
  gap: 10px;
}

.hero-status {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: #475569;
  padding: 8px 10px;
  border: 1px solid #dbe2ea;
  border-radius: 10px;
  background: #f8fafc;
}

.status-dot {
  width: 8px;
  height: 8px;
  border-radius: 999px;
  background: #16a34a;
}

.hero-metrics {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}

.hero-metric-item {
  flex: 1;
  min-width: 0;
  padding: 10px 14px;
  border: 1px solid #dbe2ea;
  border-radius: 12px;
  background: #ffffff;

  .metric-key {
    display: block;
    font-size: 12px;
    color: #64748b;
    margin-bottom: 6px;
  }

  .metric-value {
    font-size: 20px;
    font-weight: 700;
    color: #0f172a;
  }
}

.overview-card {
  background: #f8fafc;
  border: 1px solid #dbe2ea;
  border-radius: 16px;
  padding: 16px;
  display: flex;
  flex-direction: column;
  min-height: 172px;
  transition: transform 0.2s ease, box-shadow 0.2s ease, border-color 0.2s ease;
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.7);

  &:hover {
    transform: translateY(-2px);
    border-color: #c7d2e4;
    box-shadow: 0 12px 22px rgba(15, 23, 42, 0.08);
  }
}

.overview-head {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}

.overview-icon {
  width: 42px;
  height: 42px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  color: #ffffff;

  &.user {
    background: #1d4ed8;
  }
  &.expert {
    background: #7c3aed;
  }
  &.assessment {
    background: #0f766e;
  }
  &.consultation {
    background: #b45309;
  }
}

.overview-content {
  width: 100%;
  margin-top: auto;

  .overview-value {
    font-size: 36px;
    font-weight: 800;
    line-height: 1.1;
    color: #111827;
  }
  .overview-label {
    margin-top: 0;
    font-size: 13px;
    color: #334155;
    font-weight: 700;
  }
  .overview-change {
    margin-top: 8px;
    font-size: 12px;
    color: #0f766e;
    font-weight: 600;
    padding-top: 8px;
    border-top: 1px dashed #cbd5e1;
  }
}

.management-section {
  :deep(.el-tabs) {
    .el-tabs__header {
      margin: 0;
      border: 1px solid #d7dee8;
      border-bottom: none;
      padding: 12px 14px 0;
      border-radius: 14px 14px 0 0;
      background: #0f172a;
    }

    .el-tabs__nav-wrap::after {
      display: none;
    }

    .el-tabs__item {
      height: 42px;
      line-height: 42px;
      border: none;
      border-radius: 11px 11px 0 0;
      color: #cbd5e1;
      font-weight: 700;
      font-size: 13px;
      letter-spacing: 0.2px;
      padding: 0 15px;
      transition: background-color 0.2s ease, color 0.2s ease;

      &:hover {
        color: #e2e8f0;
        background: rgba(148, 163, 184, 0.18);
      }

      &.is-active {
        color: #0f172a;
        background: #f8fafc;
      }
    }

    .el-tabs__content {
      background: #ffffff;
      border: 1px solid #d7dee8;
      border-top: none;
      border-radius: 0 0 14px 14px;
    }
  }
}

.management-panel,
.settings-panel {
  background: #ffffff;
  border-radius: 0 0 14px 14px;
  padding: 24px;
}

.settings-panel {
  :deep(.el-form) {
    max-width: 860px;
  }

  :deep(.el-form-item) {
    margin-bottom: 20px;
  }

  :deep(.el-form-item__label) {
    font-size: 13px;
    font-weight: 700;
    color: #334155;
    line-height: 36px;
    padding-right: 14px;
  }

  :deep(.el-input__wrapper),
  :deep(.el-textarea__inner),
  :deep(.el-select__wrapper) {
    border-radius: 10px;
    border: 1px solid #d8e0ea;
    box-shadow: none;
    min-height: 36px;
    font-size: 13px;
  }

  :deep(.el-textarea__inner) {
    min-height: 110px;
    padding: 10px 12px;
    line-height: 1.7;
  }

  :deep(.el-switch) {
    --el-switch-on-color: #0f766e;
  }

  :deep(.el-button) {
    min-height: 36px;
    border-radius: 10px;
    font-size: 13px;
    font-weight: 700;
    padding: 0 16px;
  }
}

.management-panel {
  .panel-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    padding-bottom: 16px;
    border-bottom: 1px solid #e2e8f0;

    h3 {
      margin: 0;
      font-size: 18px;
      color: #0f172a;
      font-weight: 800;
      letter-spacing: 0.2px;
    }
  }

  .panel-actions {
    display: flex;
    gap: 12px;
    align-items: center;
  }

  :deep(.el-input) {
    .el-input__wrapper {
      min-height: 36px;
      border-radius: 10px;
      box-shadow: none;
      border: 1px solid #d8e0ea;
      padding: 1px 11px;
    }

    .el-input__inner {
      font-size: 13px;
      color: #0f172a;
    }
  }

  :deep(.el-button) {
    min-height: 36px;
    border-radius: 10px;
    font-size: 13px;
    font-weight: 700;
    letter-spacing: 0.2px;
    padding: 0 14px;
  }

  :deep(.el-table) {
    border: 1px solid #dde3ec;
    border-radius: 12px;
    overflow: hidden;
    font-size: 13px;

    &::before {
      display: none;
    }

    th {
      background: #f1f5f9;
      color: #334155;
      font-weight: 700;
      font-size: 12px;
      letter-spacing: 0.3px;
      text-transform: uppercase;
      height: 44px;
      padding: 0 12px;
      border-bottom: 1px solid #dde3ec;
    }

    td {
      color: #1e293b;
      font-size: 13px;
      height: 48px;
      padding: 0 12px;
    }

    tr:hover td {
      background: #f8fafc !important;
    }

    .el-button + .el-button {
      margin-left: 8px;
    }

    .el-button--small {
      min-height: 30px;
      padding: 0 10px;
      border-radius: 8px;
      font-size: 12px;
      font-weight: 700;
    }
  }

  :deep(.el-tag) {
    border-radius: 999px;
    border: 1px solid transparent;
    font-weight: 700;
    letter-spacing: 0.2px;

    &.el-tag--success {
      background: #ecfdf3;
      color: #166534;
      border-color: #bbf7d0;
    }

    &.el-tag--warning {
      background: #fffbeb;
      color: #92400e;
      border-color: #fde68a;
    }

    &.el-tag--info {
      background: #eff6ff;
      color: #1e40af;
      border-color: #bfdbfe;
    }

    &.el-tag--danger {
      background: #fef2f2;
      color: #991b1b;
      border-color: #fecaca;
    }
  }
}

:deep(.el-dialog) {
  border-radius: 14px;
  overflow: hidden;
  border: 1px solid #dce2eb;
  box-shadow: 0 24px 40px rgba(15, 23, 42, 0.14);

  .el-dialog__header {
    border-bottom: 1px solid #e2e8f0;
    padding: 14px 18px;
    margin-right: 0;
    background: #f8fafc;
  }

  .el-dialog__title {
    font-size: 15px;
    font-weight: 800;
    letter-spacing: 0.2px;
    color: #0f172a;
  }

  .el-dialog__body {
    padding: 18px 18px 14px;
    color: #334155;
  }

  .el-dialog__footer {
    border-top: 1px solid #eef2f7;
    padding: 12px 18px 14px;
    display: flex;
    justify-content: flex-end;
    gap: 10px;
  }

  .el-dialog__footer .el-button {
    min-height: 34px;
    border-radius: 9px;
    font-size: 13px;
    font-weight: 700;
    padding: 0 14px;
  }

  .el-dialog__footer .el-button--default {
    border-color: #d5deea;
    color: #334155;
    background: #ffffff;
  }

  .el-dialog__footer .el-button--primary {
    border-color: #1d4ed8;
    background: #1d4ed8;
    color: #ffffff;
  }

  .el-dialog__headerbtn {
    top: 15px;
  }

  .el-dialog__headerbtn .el-dialog__close {
    color: #64748b;
    font-size: 16px;
  }
}

:deep(.el-dialog__body) {
  .el-form {
    .el-form-item {
      margin-bottom: 18px;
    }

    .el-form-item__label {
      color: #334155;
      font-size: 13px;
      font-weight: 700;
      line-height: 36px;
      padding-right: 12px;
    }

    .el-input__wrapper,
    .el-select__wrapper,
    .el-textarea__inner {
      border-radius: 10px;
      border: 1px solid #d8e0ea;
      box-shadow: none;
      min-height: 36px;
      font-size: 13px;
    }

    .el-input-number {
      width: 100%;
    }

    .el-input-number .el-input__wrapper {
      min-height: 36px;
    }

    .el-textarea__inner {
      padding: 10px 12px;
      line-height: 1.7;
    }
  }
}

.reply-dialog-body {
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
    background: #f1f5f9;
    color: #334155;
    font-weight: 700;
    font-size: 12px;
    letter-spacing: 0.2px;
  }

  :deep(.el-descriptions__content) {
    color: #1e293b;
    font-size: 13px;
    line-height: 1.7;
  }

  :deep(.el-textarea__inner) {
    border-radius: 10px;
    border: 1px solid #d8e0ea;
    box-shadow: none;
    line-height: 1.7;
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

:deep(.el-rate) {
  --el-rate-icon-size: 14px;
}

:deep(.el-dialog__body strong) {
  color: #0f172a;
  font-size: 13px;
  font-weight: 700;
}

:deep(.el-dialog__body p) {
  color: #334155;
  line-height: 1.75;
}

@media (max-width: 768px) {
  .dashboard-container {
    padding: 14px;
  }

  .hero-header {
    flex-direction: column;
    gap: 12px;
  }

  .hero-meta {
    width: 100%;
    justify-content: space-between;
  }

  .hero-metrics {
    flex-direction: column;
  }

  .panel-header,
  .management-panel .panel-actions {
    flex-direction: column;
    align-items: stretch;
  }

  .management-panel :deep(.el-button) {
    width: 100%;
  }

  .settings-panel :deep(.el-form-item__label) {
    line-height: 32px;
  }

  :deep(.el-dialog) {
    width: calc(100vw - 24px) !important;
    max-width: 700px;
  }

  .overview-card {
    padding: 18px;
  }
}
</style>
