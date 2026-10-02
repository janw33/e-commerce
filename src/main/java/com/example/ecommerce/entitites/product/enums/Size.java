package com.example.ecommerce.entitites.product.enums;

import lombok.Getter;

@Getter
public enum Size {
    ONE_SIZE(SizeType.NONE),

    XXXS(SizeType.CLOTHING),
    XXS(SizeType.CLOTHING),
    XS(SizeType.CLOTHING),
    S(SizeType.CLOTHING),
    M(SizeType.CLOTHING),
    L(SizeType.CLOTHING),
    XL(SizeType.CLOTHING),
    XXL(SizeType.CLOTHING),
    XXXL(SizeType.CLOTHING),
    XXXXL(SizeType.CLOTHING),
    XXXXXL(SizeType.CLOTHING),
    XXXXXXL(SizeType.CLOTHING),
    XXXXXXXL(SizeType.CLOTHING),
    XXXXXXXXL(SizeType.CLOTHING),
    XXXXXXXXXL(SizeType.CLOTHING),

    W25(SizeType.WAIST),
    W26(SizeType.WAIST),
    W27(SizeType.WAIST),
    W28(SizeType.WAIST),
    W29(SizeType.WAIST),
    W30(SizeType.WAIST),
    W31(SizeType.WAIST),
    W32(SizeType.WAIST),
    W33(SizeType.WAIST),
    W34(SizeType.WAIST),
    W35(SizeType.WAIST),
    W36(SizeType.WAIST),
    W38(SizeType.WAIST),
    W40(SizeType.WAIST),
    W42(SizeType.WAIST),
    W44(SizeType.WAIST),
    W46(SizeType.WAIST),
    W48(SizeType.WAIST),
    W50(SizeType.WAIST),

    EU_34(SizeType.SHOES),
    EU_34_5(SizeType.SHOES),
    EU_35(SizeType.SHOES),
    EU_35_5(SizeType.SHOES),
    EU_36(SizeType.SHOES),
    EU_36_5(SizeType.SHOES),
    EU_37(SizeType.SHOES),
    EU_37_5(SizeType.SHOES),
    EU_38(SizeType.SHOES),
    EU_38_5(SizeType.SHOES),
    EU_39(SizeType.SHOES),
    EU_39_5(SizeType.SHOES),
    EU_40(SizeType.SHOES),
    EU_40_5(SizeType.SHOES),
    EU_41(SizeType.SHOES),
    EU_41_5(SizeType.SHOES),
    EU_42(SizeType.SHOES),
    EU_42_5(SizeType.SHOES),
    EU_43(SizeType.SHOES),
    EU_43_5(SizeType.SHOES),
    EU_44(SizeType.SHOES),
    EU_44_5(SizeType.SHOES),
    EU_45(SizeType.SHOES),
    EU_45_5(SizeType.SHOES),
    EU_46(SizeType.SHOES),
    EU_46_5(SizeType.SHOES),
    EU_47(SizeType.SHOES),
    EU_47_5(SizeType.SHOES),
    EU_48(SizeType.SHOES),
    EU_48_5(SizeType.SHOES),
    EU_49(SizeType.SHOES),
    EU_49_5(SizeType.SHOES),
    EU_50(SizeType.SHOES),
    EU_51(SizeType.SHOES),
    EU_52(SizeType.SHOES);

    private final SizeType type;

    Size(SizeType type) {
        this.type = type;
    }
}
