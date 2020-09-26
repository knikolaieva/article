package com.sytoss.article.model;

import java.io.Serializable;
import java.util.Objects;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ValueModel implements Serializable, Comparable<ValueModel> {

    private static final long serialVersionUID = 8569952214165446514L;
    double scale;
    double rotate;
    double shear;

    public ValueModel(double scale, double shear, double rotate) {
        this.scale = scale;
        this.rotate = rotate;
        this.shear = shear;
    }

    @Override
    public String toString() {
        return "k("+scale + ");hx(" + shear + ");a(" + rotate + ")" ;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ValueModel that = (ValueModel) o;
        return Double.compare(that.scale, scale) == 0 &&
                Double.compare(that.rotate, rotate) == 0 &&
                Double.compare(that.shear, shear) == 0;
    }

    @Override
    public int hashCode() {

        return Objects.hash(scale, rotate, shear);
    }

    @Override
    public int compareTo(ValueModel o) {
        if(this.scale != o.scale)
            return Double.compare(this.scale, o.scale);
        if(this.shear != o.shear)
            return Double.compare(this.shear, o.shear);
        if(this.rotate != o.rotate)
            return Double.compare(this.rotate, o.rotate);
        return 0;
    }
}
