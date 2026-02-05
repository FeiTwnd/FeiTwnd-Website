package cc.feitwnd.mapper;

import cc.feitwnd.annotation.AutoFill;
import cc.feitwnd.entity.Visitors;
import cc.feitwnd.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface VisitorMapper {
    /**
     * 根据访客指纹查询访客信息
     * @param fingerprint
     * @return
     */
    @Select("select * from visitors where fingerprint = #{fingerprint}")
    Visitors findVisitorByFingerprint(String fingerprint);

    /**
     * 插入访客信息
     * @param visitor
     */
    @AutoFill(value = OperationType.INSERT)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertVisitor(Visitors visitor);

    /**
     * 根据id更新访客信息
     * @param visitor
     */
    @AutoFill(value = OperationType.UPDATE)
    void updateById(Visitors visitor);
}
