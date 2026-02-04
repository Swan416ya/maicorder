// Flutter 核心组件库，提供了构建 UI 所需的基本组件
import 'package:flutter/material.dart';

/// 组件内部的状态枚举
/// 定义了签到按钮的三种状态：
/// - idle: 空闲状态，显示签到按钮
/// - success: 成功状态，显示打勾图标
/// - gone: 消失状态，组件不可见
enum _BtnStatus { idle, success, gone }

/// 签到按钮组件
/// 一个带有动画效果的签到按钮，点击后会显示成功图标，然后消失
/// 继承自 StatefulWidget，因为需要管理按钮状态的变化
class CheckBtn extends StatefulWidget {
  /// 当动画全部播放完毕（组件消失后）的回调
  /// 可选参数，用于通知父组件动画已完成
  final VoidCallback? onAnimationDone;

  /// 构造函数
  /// 接收 key 和 onAnimationDone 参数
  const CheckBtn({super.key, this.onAnimationDone});

  /// 创建状态管理对象
  /// 返回 _CheckBtnState 实例，负责管理按钮的状态和动画
  @override
  State<CheckBtn> createState() => _CheckBtnState();
}

/// CheckBtn 的状态管理类
/// 负责管理按钮状态和处理点击逻辑
class _CheckBtnState extends State<CheckBtn> {
  /// 当前按钮状态
  /// 初始状态为 idle，显示签到按钮
  _BtnStatus _status = _BtnStatus.idle;

  /// 处理按钮点击事件
  /// 执行完整的动画流程：按钮 -> 成功图标 -> 消失
  Future<void> _handleTap() async {
    // 第一步：将状态切换为成功状态，显示打勾图标
    setState(() {
      _status = _BtnStatus.success;
    });

    // 第二步：等待 1.5 秒，展示成功动画
    // 使用 Future.delayed 模拟异步等待
    await Future.delayed(const Duration(milliseconds: 1500));

    // 检查组件是否仍然挂载在组件树上
    // 如果组件已经被销毁，则不再执行后续操作
    if (!mounted) return;

    // 第三步：将状态切换为消失状态，组件不可见
    setState(() {
      _status = _BtnStatus.gone;
    });

    // 第四步：通知父组件动画已完成
    // 如果父组件传入了回调函数，则调用它
    if (widget.onAnimationDone != null) {
      widget.onAnimationDone!();
    }
  }

  /// 构建签到按钮的 UI
  @override
  Widget build(BuildContext context) {
    // AnimatedSwitcher 用于实现组件切换时的动画效果
    return AnimatedSwitcher(
      // 动画持续时间，设置为 500 毫秒
      duration: const Duration(milliseconds: 500),
      // 自定义切换动画效果
      // 组合了透明度动画和缩放动画
      transitionBuilder: (Widget child, Animation<double> animation) {
        return FadeTransition(
          // 透明度动画，从透明到不透明
          opacity: animation,
          // 嵌套缩放动画，实现弹性效果
          child: ScaleTransition(
            // 缩放动画的曲线，使用 CurvedAnimation 自定义
            scale: CurvedAnimation(
              parent: animation,
              // 正向动画使用弹性曲线，使动画效果更加生动
              curve: Curves.elasticOut,
              // 反向动画使用缓入曲线，使消失更自然
              reverseCurve: Curves.easeIn,
            ),
            // 要显示的子组件
            child: child,
          ),
        );
      },
      // 根据当前状态构建对应的子组件
      child: _buildChild(),
    );
  }

  /// 根据当前状态构建对应的子组件
  /// 使用 switch 语句根据状态返回不同的组件
  Widget _buildChild() {
    switch (_status) {
      case _BtnStatus.idle:
        // 空闲状态：显示签到按钮
        return _CheckButton(
          // 必须添加 Key，否则 AnimatedSwitcher 无法正确识别组件变化
          key: const ValueKey('btn'),
          // 传递点击回调函数
          onTap: _handleTap,
        );
      case _BtnStatus.success:
        // 成功状态：显示打勾图标
        return const _SuccessIcon(key: ValueKey('icon'));
      case _BtnStatus.gone:
        // 消失状态：显示空的 SizedBox，组件不可见
        return const SizedBox(key: ValueKey('empty'));
    }
  }
}

/// 内部私有子组件：蓝色签到按钮
/// 继承自 StatelessWidget，因为它不需要管理自己的状态
/// 使用下划线前缀表示这是私有组件，只能在当前文件中使用
class _CheckButton extends StatelessWidget {
  /// 点击回调函数
  /// 当用户点击按钮时执行
  final VoidCallback onTap;

  /// 构造函数
  /// 接收 key 和 onTap 参数，其中 onTap 是必需的
  const _CheckButton({super.key, required this.onTap});

  /// 构建签到按钮的 UI
  @override
  Widget build(BuildContext context) {
    // GestureDetector 用于检测手势，这里用于处理点击事件
    return GestureDetector(
      // 点击时执行传递进来的回调函数
      onTap: onTap,
      // 按钮的视觉效果，使用 Container 实现
      child: Container(
        // 按钮宽度
        width: 140,
        // 按钮高度
        height: 140,
        // 按钮装饰，设置渐变色、形状和阴影
        decoration: BoxDecoration(
          // 使用线性渐变作为背景色
          gradient: LinearGradient(
            // 渐变颜色：从浅蓝色到深蓝色
            colors: [const Color.fromARGB(255, 133, 196, 248), const Color.fromARGB(255, 78, 167, 255)],
            // 渐变起始位置：左上角
            begin: Alignment.topLeft,
            // 渐变结束位置：右下角
            end: Alignment.bottomRight,
          ),
          // 按钮形状，设置为圆形
          shape: BoxShape.circle,
          // 按钮阴影效果
          boxShadow: [
            BoxShadow(
              // 阴影颜色，使用半透明的蓝色
              color: Colors.blue.withOpacity(0.4),
              // 阴影模糊半径
              blurRadius: 20,
              // 阴影偏移量，向下偏移 10 像素
              offset: const Offset(0, 10),
            )
          ],
        ),
        // 文字居中对齐
        alignment: Alignment.center,
        // 按钮文字
        child: const Text(
          "Check In",
          // 文字样式
          style: TextStyle(
            // 文字颜色为白色
            color: Colors.white,
            // 文字大小
            fontSize: 20,
            // 文字粗细
            fontWeight: FontWeight.bold,
          ),
        ),
      ),
    );
  }
}

/// 内部私有子组件：绿色打勾图标
/// 继承自 StatelessWidget，因为它不需要管理自己的状态
/// 使用下划线前缀表示这是私有组件，只能在当前文件中使用
class _SuccessIcon extends StatelessWidget {
  /// 构造函数
  const _SuccessIcon({super.key});

  /// 构建成功图标的 UI
  @override
  Widget build(BuildContext context) {
    // 使用 Container 实现圆形背景
    return Container(
      // 图标宽度
      width: 140,
      // 图标高度
      height: 140,
      // 容器装饰，设置颜色和形状
      decoration: BoxDecoration(
        // 背景色，使用浅绿色
        color: Colors.green.shade100,
        // 形状为圆形
        shape: BoxShape.circle,
      ),
      // 容器子组件，使用 Check 图标
      child: Icon(
        // 打勾图标，使用圆角版本
        Icons.check_rounded,
        // 图标颜色，使用深一点的绿色
        color: Colors.green.shade600,
        // 图标大小
        size: 80,
      ),
    );
  }
}