# 借书小程序API文档（前端非正式版）

## 注册

API：`/register-member`

支持格式：JSON

请求方法：POST

**请求参数：**

| 参数名称    | 类型   | 必填 | 说明           |
| ----------- | ------ | ---- | -------------- |
| nickname    | string | 是   | 用户昵称       |
| student_id  | int    | 是   | 用户学号       |
| openid_code | string | 是   | 用户的身份标识 |

**返回参数：**

| 参数名称  | 类型 | 说明                                                  |
| --------- | ---- | ----------------------------------------------------- |
| errorcode | int  | 0表示注册成功，1表示用户已存在，2表示用户不是合法成员 |

请求示例：

**request**

```json
{
  "nickname":"张三",
  "studentid":1900300000,
  "openid_code":"1c054ecb0e947af1661e9f4ae63053c5",
}
```

**response**

```json
{
  "result":true/false,
}
```



## 登录（获取openid）

API：`/login`

请求方法：GET

支持格式：JSON

**请求参数：**

| 参数名称 | 类型          | 必填 | 说明           |
| -------- | ------------- | ---- | -------------- |
| openid_code     | string | 是   | 获取openid必备 |

**返回参数：**

| 参数名称 | 类型   | 说明               |
| -------- | ------ | ------------------ |
| result   | bool   | openid是否获取成功 |
| openid   | string | 用户的身份标识     |

请求示例：

**request**

```json
{
  "openid_code":"083Hu7ll2TMK874FU0ol2cPhVk1Hu7ls"
}
```

**response**

```json
{
  "result":true/false,
  "openid":"1c054ecb0e947af1661e9f4ae63053c5",
}
```


## 身份信息

API：`/userinfo`

支持格式：JSON

请求方法：GET

**请求参数：**

| 参数名称 | 类型   | 必填 | 说明           |
| -------- | ------ | ---- | -------------- |
| openid_code | string | 是 | 用户的身份标识 |


**返回参数：**

| 参数名称 | 类型 | 说明                 |
| -------- | ---- | -------------------- |
| result   | bool | 身份是否是已注册用户 |
| nickname   | string | 用户昵称 |
| student_id   | int | 用户学号 |

请求示例：

**request**

```json
{
    "openid":"1c054ecb0e947af1661e9f4ae63053c5"
}
```
或
```json
{
    "openid_code":"083Hu7ll2TMK874FU0ol2cPhVk1Hu7ls"
}
```

**response**

```json
{
    "result":true/false,
    "nickname":"李四",
    "student_id":"1900300000"
}
```




## 借书

API：`/borrow`

支持格式：JSON

请求方法：POST

**请求参数：**

| 参数名称 | 类型   | 必填 | 说明       |
| -------- | ------ | ---- | ---------- |
| bookid   | string | 是   | 书籍的guid |
| openid_code | string | 是   | 用户的openid_code |


**返回参数：**

| 参数名称  | 类型 | 说明                                                         |
| --------- | ---- | ------------------------------------------------------------ |
| errorcode | int  | 是否产生了错误。0表示没有错误，1表示该书不存在，2表示书本已借出 |

请求示例：

**request**

```json
{
    "bookid":"kexie_bookshelf_00Y8uXG6d2xYFtzsEfpIT84egQMN1zHD",
    "openid":"1c054ecb0e947af1661e9f4ae63053c5"
}
```

**response**

```json
{
    "errorcode":0/1/2,
}
```

## 还书

API：`/return`

支持格式：JSON

请求方法：POST

**请求参数：**

| 参数名称 | 类型   | 必填 | 说明       |
| -------- | ------ | ---- | ---------- |
| bookid | string | 是   | 书籍的guid |
| openid_code | string | 是   | 用户的openid_code |

**返回参数：**

| 参数名称  | 类型 | 说明                                                         |
| --------- | ---- | ------------------------------------------------------------ |
| errorcode | int  | 是否产生了错误。0表示没有错误，1表示该书不存在，2表示你没有借过这本书 |

请求示例：

**request**

```json
{
    "bookid":"1c054ecb0e947af1661e9f4ae63053c5",
}
```

**response**

```json
{
    "errorcode":0/1/2,
}
```

## 提交书籍信息

API：`/putbook`

支持格式：JSON

请求方法：POST

**请求参数：**

| 参数名称   | 类型   | 必填 | 说明       |
| ---------- | ------ | ---- | ---------- |
| openid_code | string | 是 | 用户的openid_code |
| bookname   | string | 是   | 书籍名称   |
| authors | string | 是   | 作者姓名   |
| isbn   | string    | 是   | ISBN号     |
| introduction   | string | 否   | 书籍简介   |
| bookid         | string | 是   | 书籍的guid |

**返回参数：**

| 参数名称 | 类型 | 说明                 |
| -------- | ---- | -------------------- |
| result   | bool | 书籍信息是否提交成功 |

请求示例：

**request**

```json
{
    "bookname":"信息论与编码",
    "authors":"曹雪虹,张宗橙",
    "isbn":"9787302440192",
    "introduction":"XXXXXXXXXXXXXXXXXXXX",
    "bookid":"kexie_bookshelf_00Y8uXG6d2xYFtzsEfpIT84egQMN1zHD",
}
```

**response**

```json
{
    "result":true/false,
}
```

## 获取所有书籍信息

API：`/bookshelf`

支持格式：JSON

请求方法：GET

**请求参数：**

| 参数名称    | 类型   | 必填 | 说明              |
| ----------- | ------ | ---- | ----------------- |
| openid_code | string | 是   | 用户的openid_code |

**返回参数：**

| 参数名称  | 类型         | 说明                   |
| --------- | ------------ | ---------------------- |
| result    | bool         | 总书架信息是否获取成功 |
| book_list | string(json) | 书籍信息               |

请求示例：

**request**

```json
{
    "openid":"1c054ecb0e947af1661e9f4ae63053c5",
}
```

**response**

```json
{
  "result":true/false,
  "book_list":[{
    "bookid":"bookid1",
    "name":"name1",
    "author":"author1",
    "introduction":"intro1",
    "availiable_cnt":0,
  },{
    "bookid":"bookid2",
    "name":"name2",
    "author":"author2",
    "introduction":"intro2",
    "availiable_cnt":0,
  },{
    "bookid":"bookid3",
    "name":"name3",
    "author":"author3",
    "introduction":"intro3",
    "availiable_cnt":1,
  },
  ],
}
```

## 获取用户已借书籍

API：`/mybooks`

支持格式：JSON

请求方法：GET

**请求参数：**

| 参数名称    | 类型   | 必填 | 说明           |
| ----------- | ------ | ---- | -------------- |
| openid_code | string | 是   | 用户的身份标识 |

**返回参数：**

| 参数名称    | 类型   | 说明                     |
| ----------- | ------ | ------------------------ |
| result      | bool   | 个人书架信息是否获取成功 |
| mybook_list | string | 我的书籍信息             |


请求示例：

**request**

```json
{
  "openid":"1c054ecb0e947af1661e9f4ae63053c5",
}
```

**response**

```json
{
  "result":true/false,
  "mybook_list":[{
    "bookid":"bookid1",
    "name":"name1",
    "author":"author1",
    "introduction":"intro1",
  },{
    "bookid":"bookid2",
    "name":"name2",
    "author":"author2",
    "introduction":"intro2",
  },{
    "bookid":"bookid3",
    "name":"name3",
    "author":"author3",
    "introduction":"intro3",
  },]
}
```