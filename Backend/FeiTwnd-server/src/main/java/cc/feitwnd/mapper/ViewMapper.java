package cc.feitwnd.mapper;

import cc.feitwnd.annotation.AutoFill;
import cc.feitwnd.entity.Views;
import cc.feitwnd.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ViewMapper {
    /**
     * 插入一条浏览记录
     * @param view
     */
    void insert(Views view);
}
