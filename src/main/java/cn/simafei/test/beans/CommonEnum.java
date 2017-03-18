package cn.simafei.test.beans;

public class CommonEnum {
    public enum Case {

        NotPut("不传参数", "不传"), IsEmpty("参数为空", "为空"), MultiEmpty("参数多空格", "多空格"), IsNullStr("参数null字符串", " is null字符串"), IsNull("参数null", " is null");
        private String name;
        private String caseDes;

        // 构造方法
        Case(String name, String caseDes) {
            this.name = name;
            this.caseDes = caseDes;
        }

        public String getCaseDes() {
            return this.caseDes;
        }
    }

}
