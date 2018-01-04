package util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

public abstract class EqualsUtil {

    // 两个对象比较时，assertEquals方法调用次数
    private static int assertCount;

    /**
     * 描述：比较两个对象所有字段内容是否相同（子对象也会进行比较）
     * <p>
     *
     * @param _obj1
     * @param _obj2
     *
     * @return boolean
     *
     * @throws Exception
     * @author ran.chunlin
     * @date 2018/1/3 21:04
     * @version 1.0
     */
    public static boolean assertEquals(Object _obj1, Object _obj2) {
        return assertEqualsIgnoreFields(_obj1, _obj2, null, true);
    }

    /**
     * 描述：比较两个对象指定字段内容是否相同（比较时只比较这些字段）
     * <p>
     *
     * @param _obj1
     * @param _obj2
     * @param _Fields 要比较的字段数组,不区分大小写
     * @param _isCascade 是否级联（true：比较深度将扩大到子对象字段；false：比较深度只限于传入对象）
     *
     * @return boolean
     *
     * @throws Exception
     * @author ran.chunlin
     * @date 2018/1/3 21:03
     * @version 1.0
     */
    public static boolean assertEqualsWithFields(Object _obj1, Object _obj2, String[] _Fields, boolean _isCascade) {
        return baseEquals(_obj1, _obj2, _Fields, _isCascade, EqualsMode.WITH_MODE);
    }

    /**
     * 描述：比较两个对象除指定字段外的其他字段内容是否相同（比较时将忽略这些字段）
     * <p>
     * 约定：比较的字段需含有get方法
     *
     * @param _obj1
     * @param _obj2
     * @param _ignoreFields 要忽略的字段数组,不区分大小写
     * @param _isCascade 是否级联（true：比较深度将扩大到子对象字段；false：比较深度只限于传入对象）
     *
     * @return boolean
     *
     * @throws Exception
     * @author ran.chunlin
     * @date 2018/1/3 20:58
     * @version 1.0
     */
    public static boolean assertEqualsIgnoreFields(Object _obj1, Object _obj2, String[] _ignoreFields,
            boolean _isCascade) {
        return baseEquals(_obj1, _obj2, _ignoreFields, _isCascade, EqualsMode.IGNORE_MODE);
    }

    /**
     * 描述：比较两个对象内容是否相同
     *
     * @param _obj1
     * @param _obj2
     * @param _Fields 字段数组
     * @param _isCascade 是否级联
     * @param _equalsMode 比较模式（WITH_MODE：只比较字段数组的字段；IGNORE_MODE：忽略字段数组的字段）
     *
     * @return boolean
     *
     * @throws Exception
     * @author ran.chunlin
     * @date 2018/1/3 20:51
     * @version 1.0
     */
    private static boolean baseEquals(Object _obj1, Object _obj2, String[] _Fields, boolean _isCascade,
            EqualsMode _equalsMode) {

        if (_obj1 == _obj2)
            return true;

        // 解决两个对象的属性互相持有对方引用或自身引用导致死循环问题。默认比较次数达到100次时，返回true（该返回是不可信的）
        if (assertCount == 100)
            return true;

        if (assertCount == 0) {//只关心传入对象各自是否为空
            if (_obj1 == null || _obj2 == null)
                return false;

            if (_obj1.getClass() != _obj2.getClass())
                return false;
        }

        if (assertCount > 0) {
            if (_obj1 != null && _obj2 != null)
                ;//子对象都不为空，继续执行比较逻辑
            else if (_obj1 == null && _obj2 == null) // 两个子对象均为空，返回TRUE
                return true;
            else //其中一个为空
                return false;
        }

        assertCount++;

        Class cla1 = _obj1.getClass();

        if (cla1.isPrimitive() || _obj1 instanceof String) // 判断cla1是否属于基本数据类型或String类型
            return _obj1.equals(_obj2);

        if (isWrapperType(cla1)) {// 判断cla1是否属于基本数据类型的封装类型

            try {
                Class clazz = (Class) cla1.getField("TYPE").get(null);
                if (clazz.isPrimitive())
                    return _obj1.equals(_obj2);
            } catch (Exception e) {// 忽略，继续向下执行
            }
        }

        // 调用类中所有字段相应的get方法进行比较
        return equalsContentByAllField(_obj1, _obj2, _Fields, _isCascade, _equalsMode);
    }

    /* 描述：比较两个对象仅内容是否相等(直接调用所有get方法进行比较) */
    @Deprecated private static boolean equalsContentByAllGet(Object _obj1, Object _obj2, String[] _Fields,
            boolean _isCascade, EqualsMode _equalsMode) {

        Method[] methods = _obj1.getClass().getMethods();// 获取类中所有方法
        Method[] methods2 = _obj2.getClass().getMethods();// 获取类中所有方法

        for (Method method : methods) {// 遍历一个对象方法数组

            if (!method.getName().startsWith("get") && !method.getName().startsWith("is")) // 非get或is方法不取
                continue;

            boolean isContain = isContainInArray(_Fields, method.getName().substring(3)) || isContainInArray(_Fields,
                    method.getName());

            if (_equalsMode == EqualsMode.WITH_MODE && !isContain) // 指定字段模式,但不包含指定字段
                continue;

            if (_equalsMode == EqualsMode.IGNORE_MODE && isContain) // 忽略字段模式,但包含指定字段
                continue;

            for (Method method2 : methods2) {// 遍历一个对象方法数组
                if (!method2.getName().startsWith("get") && !method2.getName().startsWith("is")) // 非get或is方法不取
                    continue;

                Object value = null;// 存放值
                Object value2 = null;// 存放值
                if (method.getName().equals(method2.getName())) {
                    try {
                        value = method.invoke(_obj1, new Object[] {});// 调用get方法
                    } catch (Exception e) {
                        value = null;
                    }
                    try {
                        value2 = method2.invoke(_obj2, new Object[] {});// 调用get方法
                    } catch (Exception e) {
                        value2 = null;
                    }
                    // 比较两对象是否相等，不相等返回false，相等继续下一个比较
                    if (_isCascade) { // 是否级联忽略/比较对象属性的字段
                        if (!baseEquals(value, value2, _Fields, _isCascade, _equalsMode))
                            return false;
                    } else {
                        if (!baseEquals(value, value2, null, false, EqualsMode.IGNORE_MODE))
                            return false;
                    }
                }
            }

        }

        return true;
    }

    /* 描述：比较两个对象仅内容是否相等(调用类中所有字段相应的get方法进行比较) */
    private static boolean equalsContentByAllField(Object _obj1, Object _obj2, String[] _Fields, boolean _isCascade,
            EqualsMode _equalsMode) {
        Class cla1 = _obj1.getClass();
        Class cla2 = _obj2.getClass();

        Field[] fields1 = cla1.getDeclaredFields();
        Field[] fields2 = cla2.getDeclaredFields();

        for (Field field1 : fields1) {// 遍历一个对象字段数组

            if (_equalsMode == EqualsMode.WITH_MODE && !isContainInArray(_Fields, field1.getName())) // 指定字段模式,但不包含指定字段
                continue;

            if (_equalsMode == EqualsMode.IGNORE_MODE && isContainInArray(_Fields, field1.getName())) // 忽略字段模式,但包含指定字段
                continue;

            if (!isContainInArray(fields2, field1)) // 如果另一个对象字段数组都不包含一个对象的某个字段
                return false;

            Object obj1 = getValueByField(_obj1, field1);// 获取值
            Object obj2 = getValueByField(_obj2, field1);// 获取值

            // 比较两对象是否相等，不相等返回false，相等继续下一个比较
            if (_isCascade) {// 是否级联忽略对象属性的字段比较
                if (!baseEquals(obj1, obj2, _Fields, _isCascade, _equalsMode))
                    return false;
            } else {
                if (!baseEquals(obj1, obj2, null, false, EqualsMode.IGNORE_MODE))
                    return false;
            }
        }

        return true;
    }

    /**
     * 描述: 根据字段名称从对象中取值<BR>
     *
     * @param _obj1
     * @param _field
     *
     * @return java.lang.Object
     *
     * @throws Exception
     * @author ran.chunlin
     * @date 2018/1/3 22:04
     * @version 1.0
     */
    private static Object getValueByField(Object _obj1, Field _field) {

        String sFieldName = _field.getName();// 获取字段名称
        Method[] methods = _obj1.getClass().getMethods();// 获取类中所有方法
        Object value = null;// 存放值

        for (Method method : methods) {
            if (!method.getName().startsWith("get") && !method.getName().startsWith("is"))  // 非get或is方法不取
                continue;

            if (method.getName().substring(3).equalsIgnoreCase(sFieldName) || method.getName()
                    .equalsIgnoreCase(sFieldName)) {// 找到了字段的get方法
                try {
                    value = method.invoke(_obj1, new Object[] {});// 调用get方法
                    return value;
                } catch (Exception e) {
                    return null;
                }
            }
        }
        return null;
    }

    /**
     * 描述: 判断传入类型是否是基本数据类型的封装类型
     *
     * @param _cla
     *
     * @return boolean
     *
     * @throws Exception
     * @author ran.chunlin
     * @date 2018/1/3 21:19
     * @version 1.0
     */
    private static boolean isWrapperType(Class _cla) {

        Field[] fieldArray = _cla.getFields();
        int size = fieldArray.length;

        String[] strFieldArray = new String[size];

        for (int i = 0; i < size; i++)
            strFieldArray[i] = fieldArray[i].getName();

        if (strFieldArray == null || strFieldArray.length == 0) // 如果该字符串或数组本身就为空
            return false;

        for (String strField : strFieldArray)
            if ("TYPE".equalsIgnoreCase(strField)) // 存在于数组中
                return true;

        return false;
    }

    /**
     * 描述: 数组是否包含指定数据
     *
     * @param _strs
     * @param _str
     *
     * @return boolean
     *
     * @throws Exception
     * @author ran.chunlin
     * @date 2018/1/3 22:23
     * @version 1.0
     */
    private static boolean isContainInArray(String[] _strs, String _str) {
        return Arrays.asList(_strs).contains(_str);
    }

    /**
     * 描述: 数组是否包含指定数据
     *
     * @param _fields
     * @param _field
     *
     * @return boolean
     *
     * @throws Exception
     * @author ran.chunlin
     * @date 2018/1/3 22:22
     * @version 1.0
     */
    private static boolean isContainInArray(Field[] _fields, Field _field) {
        int size = _fields.length;

        String[] strFields = new String[size];
        for (int i = 0; i < size; i++)
            strFields[i] = _fields[i].getName();

        return isContainInArray(strFields, _field.getName());
    }

}

enum EqualsMode {
    /**
     * 指定比较字段数组
     */
    WITH_MODE, /**
     * 指定忽略字段数组
     */
    IGNORE_MODE
}
