package com.example.ecommerce.entitites.product.enums;
public enum Size {

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

    EU_34(SizeType.SHOE),
    EU_34_5(SizeType.SHOE),
    EU_35(SizeType.SHOE),
    EU_35_5(SizeType.SHOE),
    EU_36(SizeType.SHOE),
    EU_36_5(SizeType.SHOE),
    EU_37(SizeType.SHOE),
    EU_37_5(SizeType.SHOE),
    EU_38(SizeType.SHOE),
    EU_38_5(SizeType.SHOE),
    EU_39(SizeType.SHOE),
    EU_39_5(SizeType.SHOE),
    EU_40(SizeType.SHOE),
    EU_40_5(SizeType.SHOE),
    EU_41(SizeType.SHOE),
    EU_41_5(SizeType.SHOE),
    EU_42(SizeType.SHOE),
    EU_42_5(SizeType.SHOE),
    EU_43(SizeType.SHOE),
    EU_43_5(SizeType.SHOE),
    EU_44(SizeType.SHOE),
    EU_44_5(SizeType.SHOE),
    EU_45(SizeType.SHOE),
    EU_45_5(SizeType.SHOE),
    EU_46(SizeType.SHOE),
    EU_46_5(SizeType.SHOE),
    EU_47(SizeType.SHOE),
    EU_47_5(SizeType.SHOE),
    EU_48(SizeType.SHOE),
    EU_48_5(SizeType.SHOE),
    EU_49(SizeType.SHOE),
    EU_49_5(SizeType.SHOE),
    EU_50(SizeType.SHOE),
    EU_51(SizeType.SHOE),
    EU_52(SizeType.SHOE);

    private final SizeType type;

    Size(SizeType type) {
        this.type = type;
    }

    public SizeType getType() {
        return type;
    }
}
