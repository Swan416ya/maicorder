import 'package:flutter/material.dart';
import '../../data/auth_service.dart';
import '../widgets/checkin_btn.dart';
import '../widgets/login_btn.dart';

class HomePage extends StatefulWidget {
  const HomePage({super.key});

  @override
  State<HomePage> createState() => _HomePageState();
}

class _HomePageState extends State<HomePage> {
  // 控制右下角按钮是否显示
  bool _showFloatBtn = false;

  /// 处理右下角按钮的点击逻辑
  void _handleFloatBtnTap() {
    if (AuthService.isLoggedIn) {
      // 逻辑分支1：如果已登录 -> 跳转个人中心
      print("用户已登录，前往 ProfilePage");
      Navigator.pushNamed(context, '/profile');
    } else {
      // 逻辑分支2：如果未登录 -> 跳转登录页
      print("用户未登录，前往 LoginPage");
      // 使用 .then 可以在用户从登录页返回时刷新页面状态（可选）
      Navigator.pushNamed(context, '/login').then((_) {
        setState(() {}); // 刷新界面，比如如果登录成功回来可能要换个头像
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.grey[100],
      appBar: AppBar(title: const Text("每日打卡"), centerTitle: true),
      body: Stack(
        children: [
          // 1. 页面中心内容：打卡按钮
          Center(
            child: CheckBtn(
              onAnimationDone: () {
                // 打卡动画结束，显示右下角按钮
                setState(() {
                  _showFloatBtn = true;
                });
                ScaffoldMessenger.of(context).showSnackBar(
                  const SnackBar(content: Text("打卡成功！")),
                );
              },
            ),
          ),

          // 2. 右下角悬浮按钮 (使用 AnimatedOpacity 做淡入效果)
          Positioned(
            right: 20,
            bottom: 40,
            child: AnimatedOpacity(
              opacity: _showFloatBtn ? 1.0 : 0.0, // 根据状态控制透明度
              duration: const Duration(milliseconds: 500),
              // IgnorePointer 防止隐藏时被误触
              child: IgnorePointer(
                ignoring: !_showFloatBtn,
                child: LoginFloatBtn(
                  // 根据登录状态显示不同的图标/图片
                  imageWidget: Icon(
                    AuthService.isLoggedIn ? Icons.person : Icons.login,
                    color: const Color.fromARGB(255, 106, 160, 253),
                    // color: const Color.fromARGB(255, 255, 17, 17),
                    size: 30,
                  ),
                  // 点击事件绑定上面定义的逻辑
                  onTap: _handleFloatBtnTap,
                ),
              ),
            ),
          ),
        ],
      ),
    );
  }
}