package com.team5.backend.domain.category.entity;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public enum KeywordType {
    // 패션의류
    TSHIRT        ("티셔츠",     CategoryType.FASHION_CLOTHES),
    DRESS         ("원피스",     CategoryType.FASHION_CLOTHES),
    SHIRT         ("셔츠",       CategoryType.FASHION_CLOTHES),
    OUTER         ("아우터",     CategoryType.FASHION_CLOTHES),
    PANTS         ("팬츠",       CategoryType.FASHION_CLOTHES),

    // 패션잡화
    SHOES         ("신발",       CategoryType.FASHION_ACCESSORY),
    BAG           ("가방",       CategoryType.FASHION_ACCESSORY),
    WALLET        ("지갑",       CategoryType.FASHION_ACCESSORY),
    HAT           ("모자",       CategoryType.FASHION_ACCESSORY),
    ACCESSORY     ("악세사리",    CategoryType.FASHION_ACCESSORY),

    // 뷰티
    SKINCARE      ("스킨케어",    CategoryType.BEAUTY),
    MASK_PACK     ("마스크팩",    CategoryType.BEAUTY),
    MAKEUP        ("메이크업",    CategoryType.BEAUTY),
    PERFUME       ("향수",       CategoryType.BEAUTY),
    HAIR_CARE     ("헤어케어",    CategoryType.BEAUTY),

    // 디지털/가전
    SMARTPHONE    ("스마트폰",    CategoryType.DIGITAL_APPLIANCE),
    TABLET        ("태블릿",     CategoryType.DIGITAL_APPLIANCE),
    LAPTOP        ("노트북",     CategoryType.DIGITAL_APPLIANCE),
    TV            ("TV",         CategoryType.DIGITAL_APPLIANCE),
    REFRIGERATOR  ("냉장고",     CategoryType.DIGITAL_APPLIANCE),
    WASHING_MACHINE("세탁기",    CategoryType.DIGITAL_APPLIANCE),

    // 가구/인테리어
    BED           ("침대",       CategoryType.FURNITURE),
    SOFA          ("소파",       CategoryType.FURNITURE),
    TABLE         ("테이블",     CategoryType.FURNITURE),
    CHAIR         ("의자",       CategoryType.FURNITURE),
    LIGHTING      ("조명",       CategoryType.FURNITURE),

    // 생활/건강
    BODY_CARE     ("바디케어",    CategoryType.LIVING),
    SUPPLEMENT    ("영양제",     CategoryType.LIVING),
    TOOTHPASTE    ("치약",       CategoryType.LIVING),
    VACUUM_CLEANER("청소기",     CategoryType.LIVING),
    DAILY_GOODS   ("생활용품",    CategoryType.LIVING),

    // 식품
    FRUIT         ("과일",       CategoryType.FOOD),
    VEGETABLE     ("채소",       CategoryType.FOOD),
    MEAT          ("정육",       CategoryType.FOOD),
    SIDE_DISH     ("반찬/간편식", CategoryType.FOOD),
    INSTANT_FOOD  ("즉석식품",    CategoryType.FOOD),
    BEVERAGE      ("음료",       CategoryType.FOOD),

    // 스포츠/레저
    SPORTSWEAR    ("운동복",     CategoryType.SPORTS),
    SNEAKERS      ("운동화",     CategoryType.SPORTS),
    EQUIPMENT     ("용품",       CategoryType.SPORTS),
    GOLF          ("골프",       CategoryType.SPORTS),
    SWIMMING      ("수영",       CategoryType.SPORTS),

    // 자동차/공구
    AUTO_ACCESSORY("자동차용품",  CategoryType.CAR),
    CAR_CARE      ("세차용품",    CategoryType.CAR),
    TOOLS         ("공구장비",    CategoryType.CAR),
    HAND_TOOL     ("수공구",      CategoryType.CAR),
    TIRE          ("타이어",     CategoryType.CAR),

    // 도서/음반/DVD
    NOVEL         ("소설",       CategoryType.BOOK),
    SELF_DEVELOP  ("자기계발",    CategoryType.BOOK),
    COMIC         ("만화/웹툰",   CategoryType.BOOK),
    ALBUM         ("음반",       CategoryType.BOOK),
    DVD           ("DVD",        CategoryType.BOOK),

    // 유아동/완구
    BABY_CLOTHES  ("유아의류",    CategoryType.KIDS),
    CHILD_CLOTHES ("아동의류",    CategoryType.KIDS),
    TOY           ("장난감",     CategoryType.KIDS),
    KIDS_BOOKS    ("도서용품",    CategoryType.KIDS),
    BABY_GOODS    ("유아용품",    CategoryType.KIDS),

    // 반려동물
    DOG_FOOD      ("강아지 사료",  CategoryType.PET),
    CAT_FOOD      ("고양이 사료",  CategoryType.PET),
    PET_SNACK     ("간식",       CategoryType.PET),
    PET_TOY       ("장난감",     CategoryType.PET),
    PET_HYGIENE   ("위생용품",    CategoryType.PET)
    ;

    private final String displayName;
    private final CategoryType parent;

    KeywordType(String displayName, CategoryType parent) {
        this.displayName = displayName;
        this.parent = parent;
    }

    /** 대분류(parent)에 속한 키워드 목록만 리턴 */
    public static List<KeywordType> ofParent(CategoryType parent) {
        return Arrays.stream(values())
                .filter(k -> k.getParent() == parent)
                .toList();
    }
}
