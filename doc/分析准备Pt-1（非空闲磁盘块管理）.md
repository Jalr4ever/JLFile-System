>为操作系统课设做的一些分析准备

- **基本概念**：

  - 文件（从逻辑结构看）：

    文件的逻辑结构称之为文件组织，有两类，用户看的就是文件的逻辑结构

    - 有结构文件（记录式文件）：由若干个相关记录组成，而记录就是描述文件的属性，记录由数据项组成，而数据项就是所谓的字段，关系：

      数据项（字段）--> 记录（属性）--> 有结构文件 

      举例：如数据库系统或信息管理系统的文件

      按文件组织方式，有结构文件有三类：

      - 顺序文件：记录按某种顺序排列形成的文件
      - 索引文件：就是索引表文件，为变长记录建立形成的表，对文件的每一个记录都设置一个表项
      - 索引顺序文件：同样为每个文件建立一张索引表，而这里的表项不是每一个记录的，而是一组记录中的第一个记录

    - 无结构文件（流式文件）：就是一个字符流，是有序相关信息项的集合，以字节为基本单位

      举例：如系统中的可执行文件，库函数，源程序

  - 文件（从物理结构看）：

    文件的物理结构称之为文件在外存的存储组织形式，也可以说就是文件的分配方式，OS 看到的就是文件的物理结构

    - 连续分配方式

      - 地址映射：假设有一个文件 A，有三块逻辑块（从0号开始），操作系统查文件 A 的 FCB，得到起始块号与长度，则物理块号 = 起始块号 + 逻辑块号

        过程：FCB --> 物理块号 = 起始块号 + 逻辑块号

      - 该分配方式支持顺序访问和随机访问

    - 链接分配方式

      - 隐式链接

        - 地址映射：假设有一个文件 A，有三块逻辑块（从0号开始），操作系统查文件 A 的 FCB，得到起始块号与结束块号，则先把 0 号逻辑块读入内存，得到 1 号逻辑块的物理块号，再把 1 号逻辑块读入内存，得到 2 号逻辑块的物理块号... 

          过程：FCB --> 起始块号开始 --> 物理块号 i --> 物理块号 i-1-->...

        - 该分配方式只支持顺序访问

      - 显示链接

        - 相比于隐式链接，添加了一个 FAT 表，记载用于链接文件各个物理块的指针，显示地存放在一张表中，一个磁盘有多少个物理块，FAT 表的表项就有多少个

        - 地址映射：假设有一个文件 A，有三块逻辑块（从0号开始），用户要访问第 i 个逻辑块号，系统先查文件 A 的 FCB，再从中查起始块号，再从中查 FAT 表，从而查到了物理块号，转换过程由于 FAT 表在内存，所以不需磁盘操作

          过程：FCB --> 起始块号 --> FAT 表 --> 物理块号

        - 该分配方式支持顺序访问，也支持随机访问

      - 索引分配方式

        - 系统为每个文件建立一张索引表，索引表存放的磁盘块叫索引块，文件数据存放的磁盘块叫数据块，索引表记录了文件的各个逻辑块对应的物理块，这个类似于内存管理中的页表，建立逻辑页面到物理页之间的映射关系，这里，一个文件（多个逻辑块）对应一张索引表

        - 地址映射：假设有一个文件 A，有三块逻辑块（从0号开始），用户要访问第 i 个逻辑块号，系统先查文件 A 的 FCB，得到索引表的所在物理块位置，调入内存，查索引表得到文件 A 的物理块号

          过程：FCB --> 索引表 --> 物理块号

        - 多级索引：因为索引块大小也是人定的，文件大小又不是固定的！一个文件对应一个索引块，当文件太大，其索引表表项太多，一个块装不下（这里磁盘是可以存很多索引块和文件数据块的，所以磁盘充足已经是大前提了！）时，就可以利用多级索引

  - 目录

    - 目录中有目录项，目录项中有：文件名和文件的各自属性的说明、文件所在的物理地址（地址指针），关系

      文件属性说明与物理地址 --> 目录项 --> 目录

  - FCB

    - 操作系统为了实现目录引入的数据结构，本质上，一个 FCB 就是一个目录项，所以，多个 FCB 构成的有序集合就是一个目录，关系：

      一个FCB（目录项）---> 多个 FCB（目录） 

    - FCB 包含的 3 类信息：
      1. 基本信息，如文件名、文件的物理位置、文件的逻辑结构、文件的物理结构等
      2. 存取控制信息，如文件存取权限
      3. 使用信息，如文件建立时间、修改时间

  - 索引结点

    - 操作系统为了实现目录引入的数据结构，简称为 "i 结点"，本质上，一个索引结点（i 结点）就是一个文件的描述信息，而一个文件名加上一个文件对应的文件描述信息就是一个 FCB，上面说一个 FCB 是一个目录项，这里用索引结点来实现的目录，目录项组成为：文件名加上指向该文件对应的 i 结点的指针（索引结点的编号），关系：

      索引结点（文件描述信息）

      索引结点的编号 （i 结点的指针）+ 文件名 -->目录项 --> 多个目录项（目录） 

      索引结点 包含的信息：

      1. 文件主标识符，拥有该文件的个人或小组的标识符
      2. 文件类型，包括普通文件、目录文件或特别文件
      3. 文件存取权限，各类用户对该文件的存取权限
      4. 文件物理地址，每个索引结点中含有 13 个地址项，即 iaddr(0) ~ iaddr(12)，它们以直接或间接方式给出数据文件所在盘块的编号
      5. 文件长度，以字节为单位
      6. 文件链接计数，在本文件系统中所有指向该文件的文件名的指针计数
      7. 文件存取时间，本文件最近被进程存取的时间、最近被修改的时间以及索引结点最近被修改的时间

- **文件系统的管理对象**：

  - 文件
  - 目录
  - 磁盘空间

- **文件系统的功能**：

  1. 对文件存储空间的管理
  2. 对文件目录的管理
  3. 用于将文件的逻辑地址转换为物理地址的机制
  4. 对文件读和写的管理
  5. 对文件的共享与保护功能

- **文件系统的操作**：
  - 创建文件：
    1. 为新文件分配必要的外存空间
    2. 在文件目录中为之创建一个目录项
    3. 目录项中记录新文件的文件名，在外存的地址等属性
  - 删除文件：
    1. 从目录中给出的文件名去查找目录得到目录项，把目录项变为空项
    2. 回收该文件所占用的存储空间
  - 读文件：
    1. 从用户给的文件名去查找目录得到目录项，得到被读文件在外存的位置
    2. 用目录项中的指针对文件读/写
  - 写文件：
    1. 从用户给的文件名去查找目录得到目录项，得到指定1文件在外存的位置
    2. 用目录项中的指针对文件读/写
  - 设置文件的读/写位置：
    1. 设置文件的读/写指针的位置
    2. 读/写文件不再每次从始端开始，顺序存取改为随机读取
- **文件系统的设计**：
  - 高层设计：从用户角度看，文件的逻辑结构设计，如何将逻辑记录构成一个逻辑文件
  - 底层设计：从系统角度看，文件的物理结构设计，如何将一个文件存储在外存上

- **文件系统的文件共享功能**：

  - 概念：文件共享使多个用户（用户就是指进程）共享同一份文件，系统中只需保留该文件的一份副本（就是磁盘只需一份这种文件）。如果系统不能提供共享功能，那么每个需要该文件的用户都要有各自的副本，会造成对存储空间的极大浪费

  - 实现方式：

    - 基于索引结点实现（硬链接）

      - 原理：由于利用索引结点实现目录时的目录项是由文件名加上指向索引结点的指针（索引结点里有各自文件的信息，物理指针，链接数之类的），那么不同用户（进程）去共享使用同一文件时，可以利用索引结点，这里说的不是利用索引结点这个数据结构，是利用基于索引结点实现的目录方式来实现，原本目录项中指向索引结点的指针只指向一个文件，是一对一的，现在可以让多个用户（进程）的目录项中的指向索引结点的指针指向一个文件，就是多对一，从而实现了多个用户控制共享一个文件，而且每个用户的文件名可以不一样，因为反正是通过指针指向索引结点来获取文件的信息的

        结论如下

        原本：一个索引结点指针 --> 文件 A

        现在：多个索引结点指针 --> 文件 A

        共享实现，文件名可以更改

        删除文件时，那么对应删除用户的索引结点指向文件 A 的指针，不会删除文件 A，所有指向文件 A的索引结点全没了，才删除文件 A

    - 基于符号链实现（软链接）

      - 原理：软链接是基于硬链接做的，假设现在有个文件 A，硬链接中，目录项的索引结点指针直接指向索引结点，然后索引结点可以取得文件 A 的信息，若现在软链接来中，需要获取文件 A，目录项的索引结点指针会指向一个 "Link" 型的文件，这个是自己新创建的，然后 "Link" 型的文件存放着指向其中一个用硬链接共享着文件 A 的用户文件的路径，操作系统每次会通过路径来访问用户文件，从而获得所需获取的文件，例如 Windows 的快捷方式就是软链接的实现

      结论如下

      硬链接：多个索引结点指针 --> 文件 A

      软链接： "Link" 型文件（路径）---> 包含文件 A 索引结点指针的目录项（用户文件）--> 文件 A的索引结点

      软链接访问比硬链接慢

- **文件系统的实现（文件的分配方式）**：

  - 原理：内存管理中，进程的逻辑地址空间被分成一页页，有逻辑页号，页内地址；对应的，外存管理中，文件的逻辑地址空间也被分成一块块，有逻辑块号，块内地址，每个文件都有自己块数的盘块，而逻辑盘块号 0 号块不一定等于或者说映射到物理盘块号 0 号，所以这才不一样，物理块号有很多很多，每个文件的逻辑盘块数是远小于它的
  - 过程：用户操作逻辑地址，但其实它是被分成一块块，由 OS 完成这个过程

