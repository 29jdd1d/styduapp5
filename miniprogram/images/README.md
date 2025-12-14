# 小程序图片资源说明

本目录用于存放微信小程序所需的静态图片资源。

## 需要准备的图片

### TabBar 图标 (建议尺寸: 81x81px)
- `tab-home.png` - 首页图标
- `tab-home-active.png` - 首页选中图标
- `tab-course.png` - 课程图标
- `tab-course-active.png` - 课程选中图标
- `tab-question.png` - 刷题图标
- `tab-question-active.png` - 刷题选中图标
- `tab-community.png` - 社区图标
- `tab-community-active.png` - 社区选中图标
- `tab-mine.png` - 我的图标
- `tab-mine-active.png` - 我的选中图标

### 通用图片
- `default-avatar.png` - 默认用户头像 (建议尺寸: 200x200px)
- `default-cover.png` - 默认课程封面 (建议尺寸: 750x420px)
- `empty.png` - 空数据占位图 (建议尺寸: 400x400px)
- `logo.png` - 应用Logo (建议尺寸: 300x300px)

### 登录页
- `login-bg.png` - 登录页背景图 (可选)

### 首页
- `banner-1.png` - 轮播图1 (建议尺寸: 750x340px)
- `banner-2.png` - 轮播图2
- `banner-3.png` - 轮播图3

## 图标建议

推荐使用以下免费图标资源:
1. [阿里巴巴矢量图标库](https://www.iconfont.cn/)
2. [Remix Icon](https://remixicon.com/)
3. [Feather Icons](https://feathericons.com/)

## 注意事项

1. 所有图片应优化压缩后使用，减少小程序包体积
2. 建议使用 PNG 格式（透明背景图标）或 JPG 格式（照片类）
3. TabBar 图标需要准备选中和未选中两套
4. 大图片考虑使用网络图片URL，减少本地资源占用
