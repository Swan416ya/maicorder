<template>
    <div class="screen">
        <div class="header">
            <h1 class="title">Bind Your <span class="indent">Maicorder Card</span></h1>
            <p class="feat">使用二维码绑定你的勤了吗账号</p>
        </div>

        <div v-if="!IsBind" class="card-wrapper" @click="handleCardClick">
            <div class="card">
                <div class="corner top-left">
                    <!-- <div class="num">2</div> -->
                    <div class="suit">♥</div>
                </div>

                <div class="center-image-container">
                    <img v-bind:src="cardImage" alt="Custom Card Art" class="card-image" />
                </div>

                <div class="corner bottom-right">
                    <!-- <div class="num">2</div> -->
                    <div class="suit">♥</div>
                </div>
            </div>
        </div>
        <div v-else class="success-card" @click="copyMaicorderId">
            <p>绑定成功√ 你的maicorderId是：</p>
            <P>{{ maicorderId || '未绑定' }}</P>
            <p color="white">---点击复制---</p>
        </div>

    <div class="btn" @click="goToB50">
        前往b50页面
    </div>

    <div v-if="!IsBind">
        <input type="text" v-model="qrcode" class="input-qrcode" placeholder="扫描舞萌二维码，全选复制后输入，最后点击卡片绑定二维码">
    </div>

    <div class="footer">
        <p>github</p>
        <p>Give Me a Happy Ending <span class="small-feat">MAICORDER</span></p>
    </div>
    </div>
</template>

<script setup>

import { ref, onMounted } from 'vue'
import cardImage from '@/assets/rev-maicorder.png'
const qrcode = ref('')
const IsBind = ref(false)
const maicorderId = ref('')
import { BASE_URL } from '@/config/index.js'
import router from '@/router/index.js'

/**
 * 前往b50页面
 */
const goToB50 = () => {
    router.push('/tool/wmB50Page')
}



/**
 * 复制maicorderId
 */
const copyMaicorderId = () => {
    if (!maicorderId.value) {
        alert('请先绑定二维码')
        return
    }
    navigator.clipboard.writeText(maicorderId.value)
    alert('复制成功')
}



onMounted(() => {
    maicorderId.value = localStorage.getItem('maicorderId') || ''
    IsBind.value = maicorderId.value !== ''
})



/**
 * 获取maicorderId
 * @param {*} QR_code 二维码
 * {
    "timestamp": "2026-04-23T21:27:40.8095039+08:00",
    "status": 200,
    "error_code": "00000",
    "message": "操作成功",
    "path": "/maimai/id",
    "data": "gAAAAABp6h5M19iV00TIDbGqbPZnp5Pd-PVw8qYNEPf5Zw_p_4Fvn4epIjVH5ZOnDe7L9-_W5N3duTPEwCKgCe7mZTifO-aiGA=="
}
 */
const getMaicorderId = async (QR_code) => {
    try {
        const res = await fetch(`${BASE_URL}/maimai/id?QR_code=${QR_code}`)
        const data = await res.json()
        console.log(data)
        if (data.status === 200) {
            maicorderId.value = data.data
            localStorage.setItem('maicorderId', data.data)
            IsBind.value = true
        }
        else {
            console.log(data)
            alert('二维码无效，请重新输入二维码')
        }

    } catch (error) {
        console.log(error)
    }
}

const handleCardClick = () => {
    // alert('点击')
    if (!qrcode.value) {
        alert('请输入二维码')
        return
    }
    getMaicorderId(qrcode.value)
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Playfair+Display:ital,wght@1,500;1,700&display=swap');

/* 全局屏幕样式 */
.screen {
    background-color: #121212;
    min-height: 100vh;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: space-between;
    padding: 4rem 1rem;
    color: #eb3d4b;
    font-family: 'Playfair Display', serif;
    overflow: hidden;
    position: relative;
}

/* 标题和底部文字样式 */
.header {
    text-align: left;
    width: 100%;
    max-width: 600px;
    z-index: 10;
}

.title {
    font-size: 4.5rem;
    line-height: 0.85;
    margin: 0;
    font-style: italic;
}

.indent {
    display: block;
    margin-left: 2rem;
}

.feat {
    font-family: sans-serif;
    font-size: 1rem;
    letter-spacing: 2px;
    margin-top: 0.8rem;
    opacity: 0.9;
}

.footer {
    text-align: center;
    font-size: 0.75rem;
    letter-spacing: 1px;
    z-index: 10;
}

.small-feat {
    opacity: 0.8;
    margin-left: 5px;
}

/* 3D 场景设定 */
.card-wrapper {
    perspective: 1200px;
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-top: -30px;
    /* 向上微调 */
}

/* 扑克牌主体 */
.card {
    width: 320px;
    height: 200px;
    background: #181818;
    border: 1px solid rgba(235, 61, 75, 0.4);
    border-radius: 12px;
    position: relative;
    /* 初始透视角度 */
    transform: rotateX(55deg) rotateZ(-35deg);
    /* 动画：浮动效果 */
    animation: float 4s ease-in-out infinite;
    box-shadow: 20px 40px 60px rgba(0, 0, 0, 0.6);
    overflow: hidden;
    /* 确保图片不溢出卡面圆角 */
}

/* 角标绝对定位 (修复后的位置) */
.corner {
    position: absolute;
    display: flex;
    flex-direction: column;
    align-items: center;
    font-weight: bold;
    z-index: 2;
    /* 确保在图片之上 */
}

.top-left {
    top: 15px;
    left: 15px;
}

.bottom-right {
    bottom: 15px;
    right: 15px;
    transform: rotate(180deg);
}

.num {
    font-size: 1.8rem;
    line-height: 1;
    color: #eb3d4b;
}

.suit {
    font-size: 1.5rem;
    color: #eb3d4b;
}

/* 关键修改：图片容器布局 */
.center-image-container {
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    width: 75%;
    /* 占用卡片宽度的 75% */
    height: 80%;
    /* 占用卡片高度的 80% */
    background-color: #1a1a1a;
    /* 图片未加载时的占位色 */
    border-radius: 8px;
    /* 图片区域圆角 */
    overflow: hidden;
    display: flex;
    align-items: center;
    justify-content: center;
}

/* 图片样式 */
.card-image {
    max-width: 100%;
    max-height: 100%;
    object-fit: cover;
    /* 确保图片填满容器并保持比例 */
    filter: sepia(30%) contrast(110%) brightness(95%);
    /* 添加一点复古和深色效果，使其融入 */
}

.input-qrcode {
    margin-top: 1rem;
    width: 80%;
    padding: 0.5rem 1rem;
    border: 1px solid #eb3d4b;
    border-radius: 8px;
    font-size: 0.8rem;
    color: #eb3d4b;
    background-color: #1a1a1a;
    /* 最大宽度 */
    max-width: 600px;
    /* 居中 */
    margin: 0 auto;
    /* 最小宽度 */
    min-width: 400px;
    /* 最小高度 */
    min-height: 100px;
}

.success-card {
    margin-top: 2rem;
    text-align: center;
    font-size: 1.2rem;
    line-height: 1.8;
    color: #eb3d4b;

    /* 边框 */
    border: 1px solid #eb3d4b;
    border-radius: 8px;
    padding: 1rem;
    background-color: #1a1a1a;

    /* 最大宽度 */
    max-width: 600px;
    /* 居中 */
    margin: 0 auto;
    /* 最小宽度 */
    min-width: 400px;

    /* 文字在换行框内 */
    word-break: break-word;
}

.btn {
    margin-top: 2rem;
    padding: 0.5rem 1rem;
    border: 1px solid #eb3d4b;
    border-radius: 8px;
    font-size: 0.8rem;
    color: #eb3d4b;
    background-color: #1a1a1a;
    /* 最大宽度 */
    max-width: 600px;
    /* 居中 */
    margin: 0 auto;
    /* 最小宽度 */
    min-width: 400px;
    

    /* 文字居中 */
    text-align: center;
}



/* 浮动动画 */
@keyframes float {

    0%,
    100% {
        transform: rotateX(55deg) rotateZ(-35deg) translateZ(0px);
        box-shadow: 20px 40px 60px rgba(0, 0, 0, 0.6);
    }

    50% {
        transform: rotateX(52deg) rotateZ(-32deg) translateZ(30px);
        box-shadow: 40px 80px 100px rgba(0, 0, 0, 0.4);
    }
}

/* 移动端适配 */
@media (max-width: 600px) {
    .title {
        font-size: 3rem;
    }

    .card {
        width: 260px;
        height: 160px;
    }

    .center-image-container {
        width: 70%;
        height: 75%;
    }
}
</style>