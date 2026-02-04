import 'package:flutter/material.dart';
import '../../data/auth_service.dart';

class ProfilePage extends StatelessWidget {
  const ProfilePage({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text("个人中心")),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            const Icon(Icons.person, size: 100, color: Colors.blue),
            const SizedBox(height: 20),
            const Text("欢迎进入个人中心", style: TextStyle(fontSize: 20)),
            const SizedBox(height: 20),
            OutlinedButton(
              onPressed: () {
                // 模拟退出登录
                AuthService.isLoggedIn = false;
                Navigator.pop(context); // 返回上一页
              },
              child: const Text("退出登录"),
            )
          ],
        ),
      ),
    );
  }
}