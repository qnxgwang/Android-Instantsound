import random
import socket
import requests
from bs4 import BeautifulSoup


class Render:
    """
    line 90 存在越界问题
    """
    final_head = ''
    final_tl = ''
    base_url_head = ''
    base_url_tail = ''
    HOME = ''
    base_url = ''
    headers = {}
    song_queue = []   #存储歌曲的id的一个数组
    emotion = {'surprise': '兴奋', 'sad': '伤感', 'neutral': '安静',
                      'happy': '快乐', 'fear': '孤独', 'disgust': '感动', 'angry': '治愈'}

    def __init__(self):
        # Render.final_head = 'https://music.163.com/outchain/player?type=2&id='
        # Render.final_tl = '&auto=1&height=66'
        Render.final_head = '://music.163.com/song/media/outer/url?id='
        Render.final_tl = ''
        Render.base_url_head = 'https://music.163.com/discover/playlist/?cat='
        Render.base_url_tail = '&order=hot&limit=35&offset='
        Render.HOME = 'https://music.163.com'
        Render.headers = {
            "user-agent": "Mozilla / 5.0(Windows NT 10.0;WOW64) AppleWebKit / 537.36(KHTML, likeGecko) "
                          "Chrome / 75.0.3770.100Safari / 537.36"
        }
        Render.song_queue = []


    @staticmethod
    def create_base_url(emotion, page=0):
        Render.base_url = Render.base_url_head \
                          + str(emotion) + Render.base_url_tail + str(page*35)


    @staticmethod
    def crawl_song(play_url):
        """ 在一个歌单页面中爬取所有的歌曲
        param play_url: 所要爬取的歌单的url地址
        """
        s = requests.session()
        html = s.get(url=play_url, headers=Render.headers).content
        soup = BeautifulSoup(html, 'lxml')  #解析html为BeautifulSoup构建的一棵树

        source = soup.find('ul', {'class':'f-hide'})  # 找到第一次出现的地方
        source = source.find_all('a')   # 找到这棵子树上的所有与a相关的分支, 即歌曲id所在处
        for song in source:
            song = str(song['href'])    # 转换为字符串--方便分割出所需要的歌曲id
            song = song.split('=')[1]
            # print(Render.final_head + song + Render.final_tl)
            Render.song_queue.append(str(song))
        # print(len(Render.song_queue))


    @staticmethod
    def choose_page(page_url):
        """ 随机选取一个页数
        :param page_url: 当前所在页(一般都是第一页)的url地址
        :return: int 随机选取的页数
        """
        html = requests.get(url=page_url, headers=Render.headers).content
        soup = BeautifulSoup(html, 'lxml')
        page_tags = soup.select('.zpgi')
        src = str(page_tags[len(page_tags)-1]).split('>')
        page_cnt = int(src[len(src)-2].split('<')[0])  # 获得歌单集的总页数

        page = random.randint(0, page_cnt)  #得到一个随机的页数, 在这页随机找一个歌单
        return int(page)


    @staticmethod
    def choose_playList(emotion, page):
        """ 根据情感要求随机选取一个歌单
        :param emotion: 要查询的情感类型
        :param page: 要查询哪一页
        :return: 完整的歌单的url地址
        """
        Render.create_base_url(emotion, page)   # 获得所需要的页面
        html = requests.get(url=Render.base_url, headers=Render.headers).content
        soup = BeautifulSoup(html, 'lxml')
        lists = soup.select('.dec a')
        cnt = len(soup.select('#m-pl-container li'))
        id = random.randint(0, cnt)
        play_url = Render.HOME + lists[id]['href']    # 确定所需歌单
        # print('所在歌单: ' + play_url)
        return play_url


    @staticmethod
    def render_songURL():
        #返回爬取到的歌曲集中随机一首歌的url
        song_id = random.randint(0, len(Render.song_queue)-1)
        return Render.final_head\
                + Render.song_queue[song_id] \
                    + Render.final_tl


    @staticmethod
    def render_song(emotion):
        """
        :param emotion: string 心情
        :return: 随机爬取到的歌曲的url
        """
        Render()
        Render.create_base_url(Render.emotion[emotion], 0)
        page = Render.choose_page(Render.base_url)
        Render.crawl_song(Render.choose_playList(Render.emotion[emotion], page))
        return Render.render_songURL()
