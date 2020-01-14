Test1,2,3,4为本机测试

ReceiveClientTest和SendClientTest用于本机或其他机器之间的测试
ReceiveClientTest和SendClientTest中的topic尽量一致，不然会影响测试结果
ReceiveClientTest中的expectArriveMessageNum为每个接收端的预计接收的信息，取决于发送端数量和topic数量

为保证测试结果和可观察，及时清空log或数据库。

最大传输时间为max，e1<=e2，忽略连接时间(绘图可知):
> 当e1<s2时，max=e1-s1+e2-s2
>
> 当s2<e1&&s1<s2时，max=e2-s1
>
> 当s1>s2时，max=e2-s1

在代码中peer=client

测试时
发送端ClientID为sendClient0,sendClient1...
主题名为topic0,topic1,topic2...
接收端为receiveClient0,receiveClient1...

