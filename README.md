# api_test

> api自动化测试框架：支持http(s)

## http(s)接口

### api-config.xml配置文件

- rootUrl: 必须的配置，api的根路径，在调用api时用于拼接，配置后，会在自动添加到用例中的url的前缀中。  
- headers: 非必须配置，配置后在调用api时会将对应的name-value值设置到所有请求的请求头中。  
- params：非必须配置，公告参数，所有用例执行前，会将params下所有的param配置进行读取并存储到公共参数池中，在用例执行时，使用特定的关键字可以获取。  
- excel:测试用例存放的位置，执行时框架会根据excel中的配置的相对路径去读取用例，多个excel表使用“；”隔开。  

	<root>
    	<rootUrl>http://open.baidu.com</rootUrl>
    	<headers>
    	    <header name="Content-Type" value="application/json"></header>
    	</headers>
		<params>
			<param name="username" value="jack"></param>
		</params>
		<excel>/api-data.xls;/api-data-2/xls</excel>
	</root>

### api-data.xls 测试用例

> api请求具体用例数据。除表头外，一行代表一个api用例。框架会依次从sheet表从左到右，从上到下进行读取并按顺序执行。

- run：标记为‘Y’时，该行数据会被读取执行。
- description：该用例描述。
- method：该api的请求方法（暂只支持get,post）。
- url：api请求路径。如：/user/get，会根据配置文件中rootUrl进行自动拼接为：http://open.baidu.com/user/get
- param：请求方法param参数。
- body：请求方法为post并且以application/json方式提交的json字符串
- verify：对于api请求response数据的验证（可使用jsonPath进行校验）。校验多个使用“；”进行隔开。
"$.errorCode=0;$.errorMessage=success"表示检验接口返回的数据中$.errorCode为"0"以及$.errorMessage为"success"。

- save：使用jsonPath对response的数据进行提取存储。
如返回数据：{"username":"chenwx","userId":"1000","age":"18"},save值为“id=$.userId;age=$.age”，该接口执行完毕后会将公共参数id的值存储为1000，age存储为18。公共参数可在后面的用例中进行使用。具体使用方法见下方高级用法。

## 高级用法

> 测试用例excel表中可以使用‘${param_name}’占位符，在执行过程中如果判断含有占位符，则会将该值替换为公共参数里面的值。
> 如param中值为{"userId":"${id}","password":"123456"}，框架在执行时将会从公共参数池中获取到id的值并替换param中的‘${id}’

## 函数助手
> 测试用例excel表中可以使用‘__funcName(args)’占位符，在执行过程中如果判断含有该占位符，且funcName存在，则会执行相应的函数后进行替换。先支持函数如下：

- __random(param1,param2):随机生成一个定长的字符串(不含中文)。param1:长度(非必填，默认为6)，param2：纯数字标识(非必填，默认为false)。
- __randomText(param1): 随机生成一个定长的字符串(含中文)。param1:长度(非必填，默认为6)
- __randomStrArr(param1,param2,param3)：随机生成一个定长字符串数组。param1:数组长度(非必填，默认为1)，param2：单个字符串长度（非必填，默认6），param3：纯数字标识(非必填，默认为false)。
- __date(param1)： 生成执行该函数时的格式化字符串。param1为转换的格式，默认为‘yyyy-MM-dd’。
- __generateStrArrByStr(param1,param2)：生成定长的字符串数组。param1:参数为数组长度 即生成参数个数，param2：字符串
- __sub(param,params...)：减数。第一个参数作为减数，其他参数均作为被减数。
- __max(params...)获取所有参数的最大值。
- __plus(params...)将所有参数进行相加。
- __multi(params...)将所有参数相乘。
- __bodyfile()：。

## 待优化

- testNg报告优化
- 执行异常拦截处理（重试机制）
- log输出
- 支持xml
- 支持session
- 支持delete，put等方法
- 支持验证状态
- 支持验证数据库
- 支持更多全局配置

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
