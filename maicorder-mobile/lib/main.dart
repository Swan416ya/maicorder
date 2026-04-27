import 'package:flutter/material.dart';
import 'presentation/pages/home_page.dart';
import 'presentation/pages/login_page.dart';
import 'presentation/pages/profile_page.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: '打卡演示',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      // 设置首页
      home: const HomePage(),
      
      // 【核心】：路由表配置
      // 这里定义了字符串路径对应的页面组件
      routes: {
        '/login': (context) => const LoginPage(),
        '/profile': (context) => const ProfilePage(),
      },
    );
  }
}