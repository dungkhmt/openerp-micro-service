package com.hust.openerp.taskmanagement.hr_management.reflection.classBinding;

import com.hust.openerp.taskmanagement.hr_management.reflection.ReflectionBindingException;
import com.hust.openerp.taskmanagement.hr_management.reflection.ReflectionHelper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public abstract class ClassBindingReflection<TKey, TValue> {
    private final Map<Class<? extends TKey>, TValue> bindingMap;
    protected abstract String getPackageNameOfKey();
    protected abstract String getPackageNameOfValue();
    protected abstract Class<TKey> getKeyClass();
    protected abstract Class<TValue> getValueClass();

    public ClassBindingReflection() {
        bindingMap = generateBindingMap();
    }

    @SuppressWarnings("unchecked")
    @SneakyThrows
    private Map<Class<? extends TKey>, TValue> generateBindingMap() {
        var map = new HashMap<Class<? extends TKey>, TValue>();
        var concretesOfValue = ReflectionHelper.getConcreteClasses(getPackageNameOfValue(), getValueClass());
        for(var concreteClass : concretesOfValue){
            var keyAnnotations = concreteClass.getAnnotationsByType(ReflectionClassKey.class);
            var constructor = concreteClass.getConstructor();
            constructor.setAccessible(true);
            var valueInstance = (TValue) constructor.newInstance();
            for(var keyAnnotation : keyAnnotations){
                if (!getKeyClass().isAssignableFrom(keyAnnotation.value())) {
                    throw new ReflectionBindingException(
                            "Key " + getKeyClass() + " is not assignable from " + keyAnnotation.value()
                    );
                }
                map.put((Class<? extends TKey>) keyAnnotation.value(), valueInstance);
            }
        }
        checkAllKeyClass(map);
        return map;
    }

    public TValue get(TKey key) {
        var keyClass = key.getClass();
        if(!bindingMap.containsKey(keyClass)){
            throw new ReflectionBindingException("Key not found: " + keyClass.getName());
        }
        return this.bindingMap.get(keyClass);
    }

    public TValue get(Class<? extends TKey> keyClass) {
        if(!bindingMap.containsKey(keyClass)){
            throw new ReflectionBindingException("Key not found: " + keyClass.getName());
        }
        return this.bindingMap.get(keyClass);
    }

    public void checkAllKeyClass(Map<Class<? extends TKey>, TValue> map){
        log.info("----------- {}: checking enum key --------------------", getClass().getSimpleName());
        var concretesOfKey = ReflectionHelper.getConcreteClasses(getPackageNameOfKey(), getKeyClass());
        for(var concreteClass : concretesOfKey){
            if(!map.containsKey(concreteClass)){
                log.warn("Key not found: {}", concreteClass.getSimpleName());
            }
            else {
                log.info("Key: {} -> Value: {}", concreteClass.getSimpleName(),
                        map.get(concreteClass).getClass().getSimpleName());
            }
        }
    }
}
