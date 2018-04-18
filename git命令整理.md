##git命令大全


### 配置相关 
git config 

***

### 查看所有配置信息
git config --list

---

### 全局配置，一台主机只需要配置一次
git config --global user.name "John Doe"  

---

### 设置默认的编辑器
git config --global core.editor D:\soft_develop\VIM\vim\vim\vim.exe

---
### 初始化工作目录（生成.git目录）
git init  

---
### 跟踪目录 将目录放到暂存中 
git add

---
### 只提交暂存中的内容
git commit -m       ## -m后接提交声明

---
### 跳过暂存直接提交
git commit -a

---
### 拷贝已经存在的项目
git clone https://github.com/libgit2/libgit2
git clone https://github.com/libgit2/libgit2 myProjectName ##给拷贝的项目取别名

---

### 检查当前文件的状态
git status
git status -s

---

### 查看已修改但是未暂存的修改细节
git diff

### 查看已暂存但是未提交的修改细节
git diff --cached 

---
### 从暂存区移除文件 同样在目录中删除该文件（重命名文件也用这个命令）
git rm
### 只是从目录中暂存区删除，git不再跟踪，单文件仍然保留在工作目录中
git rm --cached

---
### 查看日志(按时间倒序排列)
git log
### 对比每次提交的差异(可接-num 查看最近的几次)
git log -p 
### 简略的日志统计信息
git log --stat
### 设置显示格式
git log --pretty=format:"%h %s" --graph
git log --pretty=format:"%h - %an, %ar : %s"
git log --pretty=oneline  
### 另外还有按照时间作限制的选项，比如 --since 和 --until 也很有用。 例如，下面的命令列出所有最近两周内的提交：
$ git log --since=2.weeks

--这个命令可以在多种格式下工作，比如说具体的某一天 "2008-01-15"，或者是相对地多久以前 "2 years 1 day 3 minutes ago"。
--还可以给出若干搜索条件，列出符合的提交。 用 --author 选项显示指定作者的提交，用 --grep 选项搜索提交说明中的关键字。 
--（请注意，如果要得到同时满足这两个选项搜索条件的提交，就必须用 --all-match 选项。否则，满足任意一个条件的提交都会被匹配出来）
--另一个非常有用的筛选选项是 -S，可以列出那些添加或移除了某些字符串的提交。 比如说，你想找出添加或移除了某一个特定函数的引用的提交，你可以这样使用：

---
### 重新提交
git commit --amend

---
### 取消暂存
git reset HEAD 

---
### 取消修改（危险操作，不可逆，文件所有修改将消失）
git checkout

---
### 查看远程仓库服务器（-v  显示对应的url）
git remote
### 查看远程仓库
git remote show [remote-name]
### 重命名远程引用名称
git remote rename pb paul

### 添加远程仓库
git remote add <shortname> <url>
### 拉取远程仓库中你没有的信息
git fetch xx

### 拉取远程的信息合并到本地分支(更新)
git pull

### 推送更新到远程服务器(如果之前有push过，需要先pull)
git push [remote-name] [branch-name]
### 将标签一起push
git push origin [tagname]
### 将所有标签一起push
git push origin --tags

---
### 列出所有的标签
git tag
### 给某次历史提交打标签（末尾指定校验和（或部分校验和））
git tag -a v1.2 9fceb02
### 在特定的标签上创建一个分支
git checkout -b [branchname] [tagname]

---
### 给命令创建别名（以下例子使git ci == git commit）
git config --global alias.ci commit

---
### 创建分支
git branch 
### 创建分支并同时切换到创建的分支
git checkout -b 

### 查看各个分支当前所指向的对象
git log --oneline --decorate

### 分支切换（主要是切换 HEAD的指向，同时切换到该分支下进行工作）
git checkout
### 删除分支(小写d只能删除未合并的分支，大写D强制删除)
git branch -d hotfix
git branch -D
### 查看所有分支的最后一次提交
git branch -v

---