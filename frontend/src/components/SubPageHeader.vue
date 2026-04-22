<template>
  <section class="subpage-header" role="banner">
    <div class="subpage-header__glow" aria-hidden="true"></div>
    <div class="subpage-header__stars" aria-hidden="true"></div>
    <div class="subpage-header__wave" aria-hidden="true"></div>

    <div class="subpage-header__nav">
      <button class="subpage-header__back" type="button" @click="handleBack">
        <span class="subpage-header__back-arrow" aria-hidden="true">
          <svg viewBox="0 0 16 16" width="12" height="12" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M10 3L5 8L10 13" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </span>
        <span class="subpage-header__back-label">{{ backText }}</span>
      </button>

      <div class="subpage-header__eyebrow" :aria-label="eyebrow">
        <span class="subpage-header__pulse" aria-hidden="true"></span>
        <span class="subpage-header__eyebrow-text">{{ eyebrow }}</span>
      </div>
    </div>

    <div class="subpage-header__content">
      <h1 class="subpage-header__title">{{ title }}</h1>
      <p v-if="subtitle" class="subpage-header__subtitle">{{ subtitle }}</p>
    </div>
  </section>
</template>

<script setup>
import { useRouter } from 'vue-router'

const props = defineProps({
  title: {
    type: String,
    required: true
  },
  subtitle: {
    type: String,
    default: ''
  },
  backText: {
    type: String,
    default: '返回主页'
  },
  backTo: {
    type: String,
    default: '/home'
  },
  eyebrow: {
    type: String,
    default: '星语航海 · 心灵之旅'
  },
  onBack: {
    type: Function,
    default: null
  }
})

const router = useRouter()

const handleBack = () => {
  if (typeof props.onBack === 'function') {
    props.onBack()
    return
  }

  if (window.history.length > 1) {
    router.back()
    return
  }
  router.push(props.backTo)
}
</script>

<style lang="scss" scoped>
.subpage-header {
  position: relative;
  margin: 16px 0 28px;
  padding: 36px 36px 42px;
  border-radius: 24px;
  overflow: hidden;
  isolation: isolate;
  color: #f1f5f9;
  background:
    radial-gradient(ellipse at 88% 8%, rgba(99, 102, 241, 0.38), transparent 48%),
    radial-gradient(ellipse at 8% 92%, rgba(14, 165, 233, 0.28), transparent 52%),
    linear-gradient(135deg, #0b1220 0%, #141f36 45%, #0f172a 100%);
  border: 1px solid rgba(148, 163, 184, 0.18);
  box-shadow:
    0 10px 36px rgba(2, 6, 23, 0.18),
    0 2px 8px rgba(2, 6, 23, 0.1);
  animation: headerReveal 0.7s cubic-bezier(0.2, 0.8, 0.2, 1) both;
}

@keyframes headerReveal {
  from {
    opacity: 0;
    transform: translateY(-8px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.subpage-header__glow {
  position: absolute;
  top: -40%;
  right: -10%;
  width: 420px;
  height: 420px;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(129, 140, 248, 0.35), transparent 65%);
  filter: blur(24px);
  z-index: -2;
  pointer-events: none;
}

.subpage-header__stars {
  position: absolute;
  inset: 0;
  z-index: -1;
  background-image:
    radial-gradient(1.4px 1.4px at 14% 28%, rgba(255, 255, 255, 0.85), transparent 60%),
    radial-gradient(1px 1px at 72% 58%, rgba(255, 255, 255, 0.7), transparent 60%),
    radial-gradient(1.6px 1.6px at 38% 16%, rgba(224, 231, 255, 0.9), transparent 60%),
    radial-gradient(1px 1px at 88% 34%, rgba(255, 255, 255, 0.55), transparent 60%),
    radial-gradient(1.2px 1.2px at 26% 78%, rgba(255, 255, 255, 0.65), transparent 60%),
    radial-gradient(1px 1px at 58% 88%, rgba(191, 219, 254, 0.7), transparent 60%),
    radial-gradient(1.3px 1.3px at 82% 72%, rgba(255, 255, 255, 0.6), transparent 60%);
  background-repeat: no-repeat;
  opacity: 0.7;
  animation: twinkle 5s ease-in-out infinite alternate;
  pointer-events: none;
}

@keyframes twinkle {
  from { opacity: 0.55; }
  to { opacity: 0.85; }
}

.subpage-header__wave {
  position: absolute;
  left: 0;
  right: 0;
  bottom: 0;
  height: 2px;
  background: linear-gradient(
    90deg,
    transparent 0%,
    rgba(99, 102, 241, 0.0) 8%,
    rgba(99, 102, 241, 0.6) 50%,
    rgba(14, 165, 233, 0.0) 92%,
    transparent 100%
  );
  z-index: -1;
  pointer-events: none;
}

.subpage-header__nav {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 14px;
  margin-bottom: 26px;
}

.subpage-header__back {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  padding: 7px 18px 7px 9px;
  border-radius: 999px;
  border: 1px solid rgba(255, 255, 255, 0.14);
  background: rgba(255, 255, 255, 0.07);
  backdrop-filter: blur(14px);
  -webkit-backdrop-filter: blur(14px);
  color: #f1f5f9;
  font-size: 13px;
  font-weight: 700;
  letter-spacing: 0.3px;
  cursor: pointer;
  transition: transform 0.35s cubic-bezier(0.2, 0.8, 0.2, 1),
              background-color 0.3s ease,
              border-color 0.3s ease,
              box-shadow 0.3s ease;

  &:hover {
    border-color: rgba(199, 210, 254, 0.55);
    background: rgba(255, 255, 255, 0.14);
    transform: translateX(-3px);
    box-shadow: 0 10px 28px rgba(99, 102, 241, 0.28);

    .subpage-header__back-arrow {
      transform: translateX(-2px);
      background: linear-gradient(135deg, #818cf8, #38bdf8);
    }
  }

  &:focus-visible {
    outline: none;
    box-shadow: 0 0 0 3px rgba(129, 140, 248, 0.5);
  }
}

.subpage-header__back-arrow {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 26px;
  height: 26px;
  border-radius: 50%;
  background: linear-gradient(135deg, #6366f1, #8b5cf6);
  color: #ffffff;
  box-shadow: 0 4px 12px rgba(99, 102, 241, 0.45);
  transition: transform 0.35s cubic-bezier(0.2, 0.8, 0.2, 1),
              background 0.3s ease;
}

.subpage-header__eyebrow {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  padding: 7px 16px;
  border-radius: 999px;
  background: rgba(99, 102, 241, 0.14);
  border: 1px solid rgba(129, 140, 248, 0.28);
  color: #c7d2fe;
  font-size: 11px;
  font-weight: 800;
  letter-spacing: 2.4px;
}

.subpage-header__eyebrow-text {
  white-space: nowrap;
}

.subpage-header__pulse {
  position: relative;
  width: 7px;
  height: 7px;
  border-radius: 50%;
  background: #a5b4fc;
  box-shadow: 0 0 10px rgba(165, 180, 252, 0.85);

  &::after {
    content: '';
    position: absolute;
    inset: -4px;
    border-radius: 50%;
    border: 1px solid rgba(165, 180, 252, 0.5);
    animation: pulseRing 1.8s ease-out infinite;
  }
}

@keyframes pulseRing {
  0% {
    transform: scale(0.6);
    opacity: 0.9;
  }
  100% {
    transform: scale(1.9);
    opacity: 0;
  }
}

.subpage-header__content {
  max-width: 760px;
}

.subpage-header__title {
  margin: 0;
  font-size: 38px;
  font-weight: 800;
  color: #f8fafc;
  letter-spacing: -0.6px;
  line-height: 1.12;
  text-shadow: 0 2px 24px rgba(2, 6, 23, 0.4);
  background: linear-gradient(180deg, #f8fafc 0%, #cbd5e1 140%);
  -webkit-background-clip: text;
  background-clip: text;
  -webkit-text-fill-color: transparent;
}

.subpage-header__subtitle {
  margin: 14px 0 0;
  font-size: 15px;
  color: rgba(226, 232, 240, 0.78);
  font-weight: 500;
  line-height: 1.72;
  letter-spacing: 0.15px;
  max-width: 640px;
}

@media (max-width: 768px) {
  .subpage-header {
    margin: 12px 0 22px;
    padding: 26px 22px 30px;
    border-radius: 20px;
  }

  .subpage-header__nav {
    flex-direction: column-reverse;
    align-items: flex-start;
    gap: 14px;
    margin-bottom: 22px;
  }

  .subpage-header__title {
    font-size: 28px;
  }

  .subpage-header__subtitle {
    font-size: 14px;
  }

  .subpage-header__glow {
    width: 280px;
    height: 280px;
  }
}

@media (max-width: 480px) {
  .subpage-header {
    padding: 22px 18px 26px;
  }

  .subpage-header__title {
    font-size: 24px;
  }

  .subpage-header__eyebrow {
    font-size: 10px;
    letter-spacing: 1.8px;
    padding: 6px 12px;
  }
}

@media (prefers-reduced-motion: reduce) {
  .subpage-header,
  .subpage-header__stars,
  .subpage-header__pulse::after {
    animation: none;
  }
}
</style>
