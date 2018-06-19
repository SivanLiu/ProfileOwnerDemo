# ProfileOwnerDemo

原理：
## 1. 如何设置 profileOwner
参考官方文档
## 2. profileOwner 工作域和个人域如何传递数据？
* 1 像素 Activity 便于在个人域、工作域中传递数据( intent )
* LauncherApps.startMainActivity(ComponentName component, UserHandle user, Rect sourceBounds, Bundle opts): 只能启动指定用户的 mainActivity，无法传递数据
* 设置默认 Intent， 类似分享 intent ：由于个人、工作域都会响应 action, 需要在发送 action 的时，禁用发送所在域的 Activity， 待 action 发送后，启用禁用的 TransmitIntentActivity
 