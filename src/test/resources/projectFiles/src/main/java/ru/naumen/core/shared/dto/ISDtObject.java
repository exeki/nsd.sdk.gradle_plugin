package ru.naumen.core.shared.dto;

import ru.naumen.core.shared.*;
import ru.naumen.metainfo.shared.IClassFqn;

/**
 * Переписанный интерфейс NSD, отсюда удалено наследование от Map и ISProperties
 */
public interface ISDtObject extends IUUIDIdentifiable, ISTitled, HasAttrPermissions, ISHasMetainfo, HasPermissionMetaData {

    /**
     * Получить метакласс объекта в виде IClassFqn
     * @return объекта
     */
    IClassFqn getMetainfo();

    /**
     * Не знаю что это делает
     * но наверно что то важное связанное с правами
     * @param var1 не знаю
     * @param var2 не знаю
     * @return не знаю
     */
    Boolean hasAuthAttrPermission(String var1, boolean var2);

    /**
     * Не знаю что это делает
     * но наверно что то важное связанное с правами
     * @param var1 не знаю
     * @return не знаю
     */
    Boolean hasPermission(String var1);
}
