//← 根元件，放 NavBar 和 router-view
<template>
  <div id="app">
    <NavBar />
    <main class="container">
      <router-view />
    </main>
  </div>
</template>

<script setup>
import NavBar from './components/NavBar.vue'
</script>

<style>
* { box-sizing: border-box; margin: 0; padding: 0; }

body {
  font-family: 'Segoe UI', 'Microsoft JhengHei', sans-serif;
  background: #f5f6fa;
  color: #333;
}

.container {
  max-width: 1100px;
  margin: 0 auto;
  padding: 24px 16px;
}

.btn {
  display: inline-block;
  padding: 8px 20px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  transition: opacity .2s;
}
.btn:hover { opacity: .85; }
.btn-primary   { background: #3b82f6; color: #fff; }
.btn-success   { background: #22c55e; color: #fff; }
.btn-danger    { background: #ef4444; color: #fff; }
.btn-secondary { background: #6b7280; color: #fff; }
.btn:disabled  { opacity: .5; cursor: not-allowed; }

.alert {
  padding: 10px 16px;
  border-radius: 6px;
  margin-bottom: 16px;
  font-size: 14px;
}
.alert-error   { background: #fee2e2; color: #b91c1c; }
.alert-success { background: #dcfce7; color: #15803d; }

.form-group { margin-bottom: 16px; }
.form-group label {
  display: block;
  font-size: 13px;
  color: #555;
  margin-bottom: 4px;
}
.form-group input,
.form-group textarea {
  width: 100%;
  padding: 8px 12px;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 14px;
}
.form-group input:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #3b82f6;
}
</style>