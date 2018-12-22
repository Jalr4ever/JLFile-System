package Util;

import Entity.BitMap;
import Entity.FAT;
import Entity.FCB_List;
import Entity.SysFile;

import java.util.Iterator;
import java.util.Scanner;

/**
 * @program: JLFile-OS
 * @description: Control system command and the other function.
 * <p>
 * Created by Jalr on 2018/12/22.
 */
public class SysController {

    private FileController fileController = new FileController();
    private FCBController fcbController = new FCBController();

    public static String currentPath = "Users:";        //当前目录逻辑路径
    public static FCB_List _currentPath = new FCB_List("Users:");           //当前目录物理路径

    //磁盘共用的 FAT 分配表
    public static FAT file_allocate_table = new FAT();

    //磁盘共用的位示图
    public static BitMap bitMap = new BitMap();

    //根目录标记
    private FCB_List rootFolderMark;

    private Scanner scanner = new Scanner(System.in);

    public SysController() {
        _currentPath.setFolderPath("Users:");
        rootFolderMark = _currentPath;
    }

    public String getCurrentPath() {
        return this.currentPath;
    }

    public void setCurrentPath(String currentPath) {
        currentPath = currentPath;
    }

    //开始解析命令
    public void start() {

        String cmd;

        System.out.println("*********恭喜您成功打开文件系统，祝您操作愉快！！！*********");
        printBlank("输入 help 查看命令帮助信息", null);

        while (true) {
            System.out.print("\n\n" + SysController.currentPath + ">");
            cmd = scanner.nextLine().toLowerCase().trim();
            String[] param = cmd.split(" +");
            if (param[0].equals("help")) {
                processHelp(param);
            } else if (param[0].equals("exit") || param[0].equals("quit") || param[0].equals("esc")) {
                processExit(param);
                break;
            } else if (param[0].equals("createfile")) {
                processCreateFile(param);
            } else if (param[0].equals("createdir")) {
                processCreateDir(param);
            } else if (param[0].equals("cd")) {
                processChangeDir(param);
            } else if (param[0].equals("list")) {
                processList(param);
            } else {
                processError(param);
            }
        }

    }


    //此方法可以使打印的说明格式良好（使所有说明对齐），注意在instruction存在的情况下command最好不要使用中文，否则影响效果
    public void printBlank(String command, String instruction) {      //打印空格
        //command   命令
        //instruction   说明
        StringBuilder commandSb = new StringBuilder(command);
        int commandLength = command.getBytes().length;
        int length = 40;  //期望的命令长度
        while (true) {
            if (commandLength < length) {
                for (int i = 0; i < length - commandLength; i++) {
                    commandSb.append(" ");
                }
                break;
            } else {
                length += 20;
            }
        }

        System.out.print("** " + commandSb);

        if (instruction != null && instruction.length() > 0) {
            System.out.println("-" + instruction);
        }
    }

    //显示文件夹下所有文件
    private void processList(String[] param) {
        if (param.length > 2) {
            printBlank("命令使用错误，输入-HELP查看命令使用方法", null);
        } else if (param.length > 1) {
            if (param[1].equals("-help")) {
                printBlank("LIST", "显示该文件夹下所有文件夹和文件");
            } else {
                printBlank("命令使用错误，输入-HELP查看命令使用方法", null);
            }
        } else if (param.length > 0) {
            Iterator iterator = _currentPath.getFcb_list().iterator();
            SysFile tempFile;
            FCB_List tempFolder;
            while (iterator.hasNext()) {
                Object obj = iterator.next();
                try {
                    tempFile = (SysFile) obj;
                    System.out.println("** "
                            + "文件名: " + tempFile.getFileName() + "\t\t"
                            + "创建日期: " + tempFile.getFileDateTime() + "\t\t"
                            + "大小: " + tempFile.getFileSize() * BitMap.disk_block_size + " b");
                } catch (Exception e) {
                    tempFolder = (FCB_List) obj;
                    System.out.println("** "
                            + "文件夹名: " + tempFolder.getFcb_list_name() + "\t\t"
                            + "创建日期: " + tempFolder.getFcbDateTime() + "\t\t"
                            + "文件夹类型: " + tempFolder.getFcb_list_type());
                }
            }
        } else {
            printBlank("HELP", "获取帮助信息");
        }
    }


    //更改目录
    private void processChangeDir(String[] param) {
        if (param.length > 1) {
            if (param[1].equals("-help")) {
                printBlank("CD FOLDERNAME", "进入名字为FOLDERNAME的目录，..为父目录");
            } else if (param.length > 2) {
                printBlank("命令使用错误，输入-HELP查看命令使用方法", null);
            } else {
                FCB_List targetDir = _currentPath.searchFCB_ListByName(param[1]);
                if (param[1].equals("..")) {
                    if(SysController._currentPath != rootFolderMark){
                        SysController._currentPath = _currentPath.getFatherFolder();
                        SysController.currentPath = _currentPath.getFolderPath();
                    }
                    else{
                        printBlank("无法再返回上一级，这里是根目录！", null);
                    }
                } else if (targetDir == null) {
                    printBlank("指定的目录不存在", null);
                } else {
                    SysController._currentPath = targetDir;
                    SysController.currentPath = targetDir.getFolderPath();
                }
            }
        } else {
            printBlank("CD需要一个参数作为想要进入的目录的名称", null);
        }
    }

    //创建文件夹
    private void processCreateDir(String[] param) {
        if (param.length > 1) {
            if (param[1].equals("-help")) {
                printBlank("CREATEDIR FOLDERNAME", "创建名字为FOLDERNAME的文件夹");
            } else if (param.length > 2) {
                printBlank("命令使用错误，输入-HELP查看命令使用方法", null);
            } else {
                if (_currentPath.searchFCB_ListByName(param[1]) == null) {
                    fcbController.ceateFolder(param[1], currentPath + "/" + param[1], SysController._currentPath);
                } else {
                    printBlank("该名称已被占用，请更换名称重新创建文件夹", null);
                }
            }
        } else {
            printBlank("CREATEDIR需要一个参数作为文件夹名称", null);
        }
    }

    //创建文件
    private void processCreateFile(String[] param) {
        if (param.length > 1) {
            if (param[1].equals("-help")) {
                printBlank("CREATEFILE FILENAME", "创建名字为FILENAME的文本文件，并等待输入内容,输入:end结束内容");
            } else if (param.length > 2) {
                printBlank("命令使用错误，输入-HELP查看命令使用方法", null);
            } else {
                if (_currentPath.searchFileByName(param[1]) == null) {
                    System.out.println("请开始输入文件内容,输入:end结束内容");
                    StringBuilder sb = new StringBuilder();
                    String tempLine = "";
                    do {
                        sb.append(tempLine + "\n");
                        tempLine = scanner.nextLine();
                    } while (!tempLine.trim().equalsIgnoreCase(":end"));

                    //等待调用创建文件函数
                    fileController.createFile(param[1], sb.toString(), currentPath, _currentPath,file_allocate_table, bitMap);
                    System.out.println(sb.toString());
                } else {
                    printBlank("该名称已被占用，请更换名称重新创建文件", null);
                }
            }
        } else {
            printBlank("CREATEFILE需要一个参数作为文件名", null);
        }
    }

    //处理错误命令
    public void processError(String[] param) {
        if (param[0].length() == 0) {
            return;
        }
        printBlank(param[0] + "命令没有定义，输入 HELP 查看命令帮助信息", null);
    }

    //处理帮助命令
    public void processHelp(String[] param) {
        if (param.length > 1) {
            if (param[1].equals("-help")) {
                helpInformation();

            } else {
                printBlank(param[0] + "命令使用错误，输入HELP查看命令使用方法", null);
            }
        } else {
            helpInformation();
        }
    }

    public void helpInformation(){
        printBlank("anyCommand -HELP", "获取该命令的使用方法");
        printBlank("HELP", "获取帮助信息");
        printBlank("EXIT or QUIT or ESC", "退出该系统");
        printBlank("CREATEFILE FILENAME", "创建名字为FILENAME的文本文件");
        printBlank("CREATEDIR FOLDERNAME", "创建名字为FOLDERNAME的文件夹");
        printBlank("CD FOLDERNAME", "进入名字为FOLDERNAME的文件夹");
        printBlank("LIST", "显示当前文件夹下的所有文件");
    }

    //处理退出命令
    public void processExit(String[] param) {
        System.out.println("                                               :@@`            \n" +
                "                  @@`                          '@@@            \n" +
                "          .       @@@.                         ''@@            \n" +
                "          @@;      @@@                         `,@@@@#         \n" +
                "          @@@@     @@@                          '@@@@@@        \n" +
                "         @@@@@     @@@                       .@@@@@@@@+        \n" +
                "        #@@@@@     @@:                    #@@@@@@@@@           \n" +
                "       @@@@@@     `@@@+                    ,@@@@@@@            \n" +
                "     @@@@@+       :@@@@@#                   @@@@@@: ,'         \n" +
                "    +@@@@        @@@@@@@@                +`     @@@@@@@`       \n" +
                "    #@@@@@    @@@@@@@@@@`               #@    ;@@@@@#@@@.      \n" +
                "    #@@@#@@   @@@@@@@;                  @@, #@@@@@;  @@@@      \n" +
                "    ,@+ @@@: :' @@@@'                   #@@@@@@@@@@ #@@@.      \n" +
                "       +@@@' '   .@@`   #;              ,@@@@   @@.@@@`        \n" +
                "      :@@@# +    @@@@@@@@@,              @@@@   @@@@@@`        \n" +
                "     @@@@@  .  +@@@@@@@@@@+               #@@@@@@@@#;.         \n" +
                "    ,@@@@@@@#@@@@@@@@#.  ,                 @@@@@@@             \n" +
                "    :@@@@@@@@@@@'@'                        :@@@`@@             \n" +
                "    `@@+@@@.@#  @   ,@@                     @@;;@@             \n" +
                "     @` @@@    @@ '@@@@@                   #@@+@@@@@@.         \n" +
                "       ,@@+   ,@@@@@@@@@@                #;@@   @@@@@@@@#      \n" +
                "       @@@    #@@@@@  @@@                `@@:   @@   +@@@@@    \n" +
                "       @@@    #@@    #@@+               #@@;    @@     ,@@@@   \n" +
                "      @@@.    +@@    @@@              +@@@   , `@@       #@@@  \n" +
                "      @@@     ,@@  ;@@@                      `@:@@         +@  \n" +
                "      @@'      @@@@@@@@                       @@@@             \n" +
                "      @@       @@@@@@@@`                      `@@@             \n" +
                "      ,`        @`                             #@@             \n" +
                "                                                @#    ");
    }

}