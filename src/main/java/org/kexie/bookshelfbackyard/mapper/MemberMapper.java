package org.kexie.bookshelfbackyard.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.kexie.bookshelfbackyard.model.Member;
import org.kexie.bookshelfbackyard.model.MemberExample;

@Mapper
public interface MemberMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table members
     *
     * @mbg.generated Sat Sep 25 20:51:01 CST 2021
     */
    long countByExample(MemberExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table members
     *
     * @mbg.generated Sat Sep 25 20:51:01 CST 2021
     */
    int deleteByExample(MemberExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table members
     *
     * @mbg.generated Sat Sep 25 20:51:01 CST 2021
     */
    int deleteByPrimaryKey(String stuId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table members
     *
     * @mbg.generated Sat Sep 25 20:51:01 CST 2021
     */
    int insert(Member record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table members
     *
     * @mbg.generated Sat Sep 25 20:51:01 CST 2021
     */
    int insertSelective(Member record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table members
     *
     * @mbg.generated Sat Sep 25 20:51:01 CST 2021
     */
    List<Member> selectByExample(MemberExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table members
     *
     * @mbg.generated Sat Sep 25 20:51:01 CST 2021
     */
    Member selectByPrimaryKey(String stuId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table members
     *
     * @mbg.generated Sat Sep 25 20:51:01 CST 2021
     */
    int updateByExampleSelective(@Param("record") Member record, @Param("example") MemberExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table members
     *
     * @mbg.generated Sat Sep 25 20:51:01 CST 2021
     */
    int updateByExample(@Param("record") Member record, @Param("example") MemberExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table members
     *
     * @mbg.generated Sat Sep 25 20:51:01 CST 2021
     */
    int updateByPrimaryKeySelective(Member record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table members
     *
     * @mbg.generated Sat Sep 25 20:51:01 CST 2021
     */
    int updateByPrimaryKey(Member record);
}