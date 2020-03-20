#1.iot-manager

http服务器，支持集群部署</br>
用于设备预登陆，登陆鉴权和路由等</br>

#2.iot-pt

TCP服务器，支持集群部署，需要zk维护多节点</br>
用于iot或im客户端连接等</br>

支持客户端消息上下行等消息透传</br>
支持节点客户端数量最大连接数配置</br>
支持节点全局消息限流</br>
支持节点单个客户端消息限流</br>
支持下行消息ACK</br>


需要组件 zk redis</br>

#协议解析
{</br>
  "service_name": "login",</br>
  "client_id": "camera_xst007",</br>
  "ack_id": "ack_188890",</br>
  "content": {}</br>
}
service_name:协议名称,除login之外可以自定义(必传)</br>
client_id:客户端编号(必传)</br>
ack_id:交互编号,请求-响应机制的上下行交互匹配(必传)</br>
content:协议自定义字段(非必传)</br>