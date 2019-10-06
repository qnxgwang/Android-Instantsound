# accept()函数的返回值
import socket
import struct

from Render import Render

class Communicator:
    ip = ''
    port = 0
    data = ''

    def __init__(self, port):
        Communicator.ip = 'localhost'
        Communicator.port = port


    @staticmethod
    def serve(ip, port):
        """ 支持多个客户端并发访问的服务端功能
        :param ip: string 服务端的ipv4地址
        :param port: int 服务端的端口
        """
        print(ip + ' ' + str(port))
        server = socket.socket(family=socket.AF_INET, type=socket.SOCK_STREAM)
        server.bind((ip, port))
        server.listen(5)    # 设置最大的连接数

        while True:
            print('server waiting for data...')
            # 接收数据 socket 与 address
            # socket的绑定地址就是客户端, 所以可以直接通过这个socket对客户端发送信息
            cus, cus_address = server.accept()
            print('Successfully connected from: ', cus_address)

            emotion = cus.recv(1024).decode('gbk')
            song_url = str(Render.render_song(emotion))
            print('长度 = ' + str(len(song_url)) + '\n爬取到的连接 = ' + song_url)

            cus.send(str(len(song_url)).encode('gbk'))  # int数字转换为str, 再转换为bytes
            cus.send(song_url.encode('gbk'))
            cus.close()


    def serving_a_cus(self, cus_socket):
        while True:
            data = cus_socket.recv(1024).decode('gbk')
            if data == 'exit' or not data:
                break
            cus_socket.send('success...'.encode('gbk'))
        cus_socket.close()


Communicator(8000)
Communicator.serve('192.168.31.153', Communicator.port)












