import 'package:flutter/material.dart';

/// 封装的登录/功能悬浮按钮组件
/// 位于页面右下角，支持嵌入图片
class LoginFloatBtn extends StatelessWidget {
  final VoidCallback onTap; // 点击后的回调
  final Widget imageWidget; // 嵌入的图片组件
  final double size; // 按钮的大小

  const LoginFloatBtn({
    super.key,
    required this.onTap,
    required this.imageWidget,
    this.size = 60.0, // 默认大小
  });

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: onTap,
      child: Container(
        width: size,
        height: size,
        //以此装饰容器，使其看起来像一个悬浮按钮
        decoration: BoxDecoration(
          color: Colors.white, // 背景色
          shape: BoxShape.circle, // 圆形
          boxShadow: [
            BoxShadow(
              color: Colors.black.withOpacity(0.15), // 阴影颜色
              blurRadius: 10, // 模糊半径
              offset: const Offset(0, 4), // 阴影偏移
            ),
          ],
        ),
        // 使用 ClipOval 确保图片被裁剪成圆形
        child: ClipOval(
          child: Padding(
            padding: const EdgeInsets.all(10.0), // 图片与边缘的内边距
            child: imageWidget, // 嵌入外部传入的图片
          ),
        ),
      ),
    );
  }
}