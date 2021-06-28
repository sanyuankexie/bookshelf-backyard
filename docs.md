# 借书小程序API文档（前端非正式版）

## 登录（获取openid）

接口地址：https://bookshelf.kexie.space/login

支持格式：JSON

请求方法：GET

**请求参数：**

| 参数名称 | 类型          | 必填 | 说明           |
| -------- | ------------- | ---- | -------------- |
| code     | string(maybe) | 是   | 获取openid必备 |

**返回参数：**

| 参数名称 | 类型   | 说明               |
| -------- | ------ | ------------------ |
| status   | bool   | openid是否获取成功 |
| openid   | string | 用户的身份标识     |

请求示例：

**request**

```json
{
    "code":"我没查过应该是string"
}
```

**response**

```json
{
    "status":true,
    "openid":"1c054ecb0e947af1661e9f4ae63053c5",
}
```

## 身份确认

接口地址：https://bookshelf.kexie.space/identityconfirm

支持格式：JSON

请求方法：GET

**请求参数：**

| 参数名称 | 类型   | 必填 | 说明           |
| -------- | ------ | ---- | -------------- |
| confirm  | string | 是   | 用户的身份标识 |

**返回参数：**

| 参数名称 | 类型 | 说明                 |
| -------- | ---- | -------------------- |
| status   | bool | 身份是否是已注册用户 |

请求示例：

**request**

```json
{
    "code":"我没查过应该是string"
}
```

**response**

```json
{
    "status":true,
}
```

## 注册

接口地址：https://bookshelf.kexie.space/register

支持格式：JSON

请求方法：POST

**请求参数：**

| 参数名称  | 类型   | 必填 | 说明           |
| --------- | ------ | ---- | -------------- |
| nickname  | string | 是   | 用户姓名       |
| studentid | int    | 是   | 用户学号       |
| openid    | string | 是   | 用户的身份标识 |

**返回参数：**

| 参数名称  | 类型   | 说明             |
| --------- | ------ | ---------------- |
| status    | bool   | 注册是否获取成功 |
| nickname  | string | 用户姓名         |
| studentid | int    | 用户学号         |

请求示例：

**request**

```json
{
    "nickname":"张三",
    "studentid":1900300000,
    "openid":"1c054ecb0e947af1661e9f4ae63053c5",
}
```

**response**

```json
{
    "status":true,
    "nickname":"张三",
    "studentid":1900300000,
}
```



## 借书

接口地址：https://bookshelf.kexie.space/borrowbooks

支持格式：JSON

请求方法：GET

**请求参数：**

| 参数名称 | 类型   | 必填 | 说明       |
| -------- | ------ | ---- | ---------- |
| borrow   | string | 是   | 书籍的guid |

**返回参数：**

| 参数名称 | 类型 | 说明                 |
| -------- | ---- | -------------------- |
| status   | bool | 书籍信息是否获取成功 |

请求示例：

**request**

```json
{
    "borrow":"kexie_bookshelf_00Y8uXG6d2xYFtzsEfpIT84egQMN1zHD",
}
```

**response**

```json
{
    "status":true,
}
```

## 还书

接口地址：https://bookshelf.kexie.space/returnbook

支持格式：JSON

请求方法：POST

**请求参数：**

| 参数名称 | 类型   | 必填 | 说明       |
| -------- | ------ | ---- | ---------- |
| guid     | string | 是   | 书籍的guid |

**返回参数：**

| 参数名称 | 类型 | 说明             |
| -------- | ---- | ---------------- |
| status   | bool | 书籍是否归还成功 |

请求示例：

**request**

```json
{
    "myookshelf":"1c054ecb0e947af1661e9f4ae63053c5",
}
```

**response**

```json
{
    "status":true,
}
```

## 提交书籍信息

接口地址：https://bookshelf.kexie.space/bookinf

支持格式：JSON

请求方法：POST

**请求参数：**

| 参数名称   | 类型   | 必填 | 说明       |
| ---------- | ------ | ---- | ---------- |
| bookname   | string | 是   | 书籍名称   |
| authorname | string | 是   | 作者姓名   |
| codename   | int    | 是   | ISBN号     |
| textarea   | string | 否   | 书籍简介   |
| QR         | string | 是   | 书籍的guid |

**返回参数：**

| 参数名称 | 类型 | 说明                 |
| -------- | ---- | -------------------- |
| status   | bool | 书籍信息是否提交成功 |

请求示例：

**request**

```json
{
    "bookname":"信息论与编码",
    "authorname":"曹雪虹，张宗橙",
    "codename":9787302440192,
    "textarea":"XXXXXXXXXXXXXXXXXXXX",
    "QR":"kexie_bookshelf_00Y8uXG6d2xYFtzsEfpIT84egQMN1zHD",
}
```

**response**

```json
{
    "status":true,
}
```

## 获取总书架信息

接口地址：https://bookshelf.kexie.space/bookshelf

支持格式：JSON

请求方法：GET

**请求参数：**

| 参数名称  | 类型   | 必填 | 说明         |
| --------- | ------ | ---- | ------------ |
| bookshelf | string | 是   | 用户的openid |

**返回参数：**

| 参数名称       | 类型   | 说明                               |
| -------------- | ------ | ---------------------------------- |
| status         | bool   | 总书架信息是否获取成功             |
| initials_list  | string | 书籍首字母的一维数组               |
| bookshelf_list | string | 书籍首字母和书籍名称混合的二维数组 |
| initials       | sting  | 书籍首字母                         |
| title          | sting  | 书籍名称                           |

请求示例：

**request**

```json
{
    "bookShelf":"1c054ecb0e947af1661e9f4ae63053c5",
}
```

**response**

```json
{
    "status":true,
    "initials_list":["A","B","C"],
    "bookshelf_list":["initials":"S","bookshelf":[{"title":"数据结构","title":"数据库系统概论"}],"initials":"J","bookshelf":[{"title":"计算机网络","title":"计算机组成原理"}]]
}
```

## 获取个人书架信息

接口地址：https://bookshelf.kexie.space/mybookshelf

支持格式：JSON

请求方法：GET

**请求参数：**

| 参数名称   | 类型   | 必填 | 说明           |
| ---------- | ------ | ---- | -------------- |
| myookshelf | string | 是   | 用户的身份标识 |

**返回参数：**

| 参数名称         | 类型   | 说明                             |
| ---------------- | ------ | -------------------------------- |
| status           | bool   | 个人书架信息是否获取成功         |
| mybookshelf_list | string | 书籍名称和书籍guid构成的二维数组 |
| mybookname       | string | 书籍名称                         |
| mybookid         | string | 书籍的guid                       |

请求示例：

**request**

```json
{
    "myookshelf":"1c054ecb0e947af1661e9f4ae63053c5",
}
```

**response**

```json
{
    "status":true,
    "mybookshelf_list":[{"mybookname":"数据结构","mybookid":"kexie_bookshelf_00Y8uXG6d2xYFtzsEfpIT84egQMN1zHD"},{"mybookname":"数计算机导论","mybookid":"kexie_bookshelf_00Y8uXG6d2xYFtzsEfpIT84egQMN1zHD"}],
}
```

