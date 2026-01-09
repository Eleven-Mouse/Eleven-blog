<template>
  <div class="login-box">
    <h2 class="text">后台管理登录</h2>
    <div class="form-item">
      <el-input
        v-model="form.username"
        type="text"
        placeholder="请输入用户名 (admin)"
      />
    </div>
    <div class="form-item">
      <el-input
        v-model="form.password"
        type="password"
        placeholder="请输入密码 (123456)"
      />
    </div>
    <el-button @click="handleLogin">登录</el-button>
    <p v-if="errorMsg" style="color: red">{{ errorMsg }}</p>
  </div>
</template>
<script>
export default {
  name: "MyLogin",
};
</script>
<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import { useAuthStore } from "../store/auth";
import request from "../utils/request";
const router = useRouter();
const authStore = useAuthStore();

const form = ref({
  username: "",
  password: "",
});
const errorMsg = ref("");

const handleLogin = async () => {
  try {

    const data = await request.post("/auth/login", form.value);

    console.log("登录成功，返回数据：", data);

    // 存 Token
    authStore.setTokens(data.accessToken, data.refreshToken);

    // 跳转首页
    router.push("/");
  } catch (error) {
    console.error(error);
    errorMsg.value = "登录失败：" + (error.message || "未知错误");
  }
};
</script>

<style scoped>
.login-box {
  width: 300px;
  margin: 100px auto;
  text-align: center;
}
.form-item {
  margin-bottom: 15px;
}
input {
  padding: 8px;
  width: 100%;
}
button {
  padding: 8px 20px;
  cursor: pointer;
}
.text {
  font-size: 1.8rem;
  font-weight: 600;
  color: var(--app-text-color, #545252);
  margin-top: 0;
  margin-bottom: 25px;
}
</style>
