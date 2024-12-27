package openerp.openerpresourceserver.reflection.enumBinding;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import openerp.openerpresourceserver.reflection.ReflectionBindingException;
import openerp.openerpresourceserver.reflection.ReflectionHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
public abstract class EnumBindingReflection<TKey extends Enum<TKey>, TValue> {
    private final Map<TKey, TValue> bindingMap;
    protected abstract String getPackageNameOfValue();
    protected abstract Class<TKey> getKeyClass();
    protected abstract Class<TValue> getValueClass();

    public EnumBindingReflection() {
        bindingMap = generateBindingMap();
    }

    @SneakyThrows
    private Map<TKey, TValue> generateBindingMap() {
        var map = new HashMap<TKey, TValue>();
        var concretesOfValue = ReflectionHelper.getConcreteClasses(getPackageNameOfValue(), getValueClass());
        for(var concreteClass : concretesOfValue){
            var keyAnnotations = concreteClass.getAnnotationsByType(ReflectionEnumKey.class);
            var constructor = concreteClass.getConstructor();
            constructor.setAccessible(true);
            var valueInstance = (TValue) constructor.newInstance();
            for(var keyAnnotation : keyAnnotations){
                var key = TKey.valueOf(getKeyClass(), keyAnnotation.value());
                if(map.containsKey(key)){
                    throw new ReflectionBindingException("Duplicate key: " + key);
                }
                map.put(key, valueInstance);
            }
        }
        checkAllEnumKey(map);
        return map;
    }

    public TValue get(TKey key) {
        if(!bindingMap.containsKey(key)){
            throw new ReflectionBindingException("Key not found: " + key);
        }
        return this.bindingMap.get(key);
    }

    public Set<TKey> getKeySet(){
        return bindingMap.keySet();
    }

    private void checkAllEnumKey(Map<TKey, TValue> map) {
        if (getKeyClass().isEnum()) {
            log.info("----------- {}: checking enum key --------------------", getClass().getSimpleName());
            TKey[] enumValues = getKeyClass().getEnumConstants();
            for (TKey key : enumValues) {
                if(!map.containsKey(key)){
                    log.warn("Key not found: {}", key);
                }
                else {
                    log.info("Key: {} -> Value: {}", key, map.get(key).getClass().getSimpleName());
                }
            }
        } else {
            throw new ReflectionBindingException("TKey is not an enum");
        }
    }

}
