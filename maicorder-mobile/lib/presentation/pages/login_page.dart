import 'package:flutter/material.dart';
import '../../data/auth_service.dart'; // 导入模拟的状态

class LoginPage extends StatelessWidget {
  const LoginPage({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text("登录页面")),
      body: Center(
        child: ElevatedButton(
          onPressed: () {
            // 模拟登录成功操作
            AuthService.isLoggedIn = true; 
            print("登录成功！");
            
            // 登录成功后，跳转到个人中心，并移除当前的登录页路由（防止点返回键回到登录页）
            Navigator.pushReplacementNamed(context, '/profile');
          },
          child: const Text("点击模拟登录"),
        ),
      ),
    );
  }
}