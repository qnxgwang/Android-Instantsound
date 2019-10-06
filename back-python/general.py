import os
import xlwt
import openpyxl

# 为何类中的非static类型的函数参数要加self

class IOExcel:
    directory = ''

    # 初始化创建一个文件夹保存excel
    def __init__(self):
        IOExcel.directory = os.getcwd() + os.path.sep + 'songs_directory'
        print(IOExcel.directory)
        print('Creating directory ' + IOExcel.directory)
        if not os.path.exists(IOExcel.directory):
            os.makedirs(IOExcel.directory)
        self.create_excel()


    def create_excel(self):
        work_book = xlwt.Workbook()  # 新建工作本
        sheet = work_book.add_sheet('sheet_name')
        sheet.write(0, 0, 'hello world')
        file_path = IOExcel.directory + os.path.sep+'songs.xls'
        work_book.save(file_path)


    # @staticmethod
    # def append_to_excel(file_path, data, emotion):

IOExcel()







