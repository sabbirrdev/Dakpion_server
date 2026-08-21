package com.company.efood.sys.repository;

import com.company.efood.base.BaseDropdownModel;
import com.company.efood.sys.entity.MenuItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuItemRepo extends JpaRepository<MenuItem, Long> {

    @org.springframework.data.jpa.repository.Query("SELECT m FROM MenuItem m WHERE LOWER(m.name) LIKE LOWER(CONCAT('%',:q,'%')) AND m.active = true")
    java.util.List<MenuItem> searchActiveByName(@org.springframework.data.repository.query.Param("q") String q);

    String findPageByAppUserIdQuery = "select  a.id, a.active, a.entry_app_user_code, a.entry_date, a.entry_user, a.update_app_user_code, a.update_date, a.update_user,\n" +
            "a.name, a.bangla_name, a.icon, a.menu_type,\ta.menu_type_name, a.serial_no,a.menu_url, a.parent_id,\n" +
            "b.is_insert, b.is_update, b.is_delete, b.is_view, b.is_approve\n" +
            "from sya_menu_item a, sya_user_role_details b, sya_user_role_master c,\n" +
            "\t sya_user_role_assign_details d, sya_user_role_assign_master e\n" +
            "where 1=1\n" +
            "and a.id = b.menu_item_id\n" +
            "and b.master_id = c.id\n" +
            "and d.user_role_id = c.id\n" +
            "and d.master_id = e.id\n" +
            "and a.menu_type = 3\n" +
            "and e.app_user_id = :appUserId\n" +
            "order by parent_id, serial_no";

    @Query(value = findPageByAppUserIdQuery, nativeQuery = true)
    List<MenuItem> findMenuItemByUserId(@Param("appUserId") Long appUserId);

    String dropdownQuery = "select a.id, a.name as name,\r\n"
            +"a.menu_type as extra\n"
            + "from sya_menu_item a \r\n"
            + "where 1=1\r\n"
            + "and a.active = true\r\n"
            + "order by a.id desc";

    @Query(value = dropdownQuery, nativeQuery = true)
    List<BaseDropdownModel> findDropdownModel();

    String searchQuery = "select a.*\n"
            + "from sya_menu_item a \n"
            + "left outer join sya_menu_item b on a.parent_id = b.id\n"
            + "where 1=1\n"
            + "and concat(a.name, a.bangla_name, a.serial_no, a.url, a.menu_type_name, b.name) ilike  %:searchValue%\n";
    @Query(value = searchQuery, nativeQuery = true)
    Page<MenuItem> searchPageableList(
            @Param("searchValue") String searchValue,
            Pageable pageable
    );


    String dropdownByMenuTypeQuery = "select a.id, concat(a.name, ' (', a.menu_type_name, ')', ' (', b.name , ')' )  as name\n" +
            "from sya_menu_item a \n" +
            "left outer join sya_menu_item b on a.parent_id = b.id\n" +
            "where 1=1\n" +
            "and a.active = true \n" +
            "and a.menu_type in (:menuType)\n" +
            "order by a.menu_type_name";
    @Query(value = dropdownByMenuTypeQuery, nativeQuery = true)
    List<BaseDropdownModel> findDropdownModelByMenuType(@Param( value = "menuType") List<Integer> menuTypeList);


    String moduleDropdownQuery = """
            select a.id, a.name as name
            from sya_menu_item a\s
            where 1=1
            and a.active = true
            and a.parent_id is  null\s
            order by a.id desc""";
    @Query(value = moduleDropdownQuery, nativeQuery = true)
    List<BaseDropdownModel> findModuleDropdownModel();
}
