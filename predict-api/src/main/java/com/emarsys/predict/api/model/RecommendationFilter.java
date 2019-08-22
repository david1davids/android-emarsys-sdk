package com.emarsys.predict.api.model;

import androidx.annotation.NonNull;

import com.emarsys.core.util.Assert;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class RecommendationFilter {

    private String type;
    private String field;
    private String comparison;
    private List<String> expectations;

    private static final String IS = "IS";
    private static final String IN = "IN";
    private static final String HAS = "HAS";
    private static final String OVERLAPS = "OVERLAPS";

    RecommendationFilter(String type, String field, String comparison, List<String> expectations) {
        this.type = type;
        this.field = field;
        this.comparison = comparison;
        this.expectations = expectations;
    }

    RecommendationFilter(String type, String field, String comparison, String expectation) {
        this.type = type;
        this.field = field;
        this.comparison = comparison;
        this.expectations = Collections.singletonList(expectation);
    }

    public static Exclude exclude(@NonNull String field) {
        Assert.notNull(field, "Field must not be null!");

        return new Exclude(field);
    }

    public static Include include(@NonNull String field) {
        Assert.notNull(field, "Field must not be null!");

        return new Include(field);
    }

    public String getType() {
        return type;
    }

    public String getField() {
        return field;
    }

    public String getComparison() {
        return comparison;
    }

    public List<String> getExpectations() {
        return expectations;
    }

    public static class Exclude {

        private static final String TYPE = "EXCLUDE";

        protected final String field;

        private Exclude(String field) {
            this.field = field;
        }

        public RecommendationFilter is(@NonNull String expectation) {
            Assert.notNull(expectation, "Expectation must not be null!");

            return new RecommendationFilter(TYPE, field, IS, expectation);
        }

        public RecommendationFilter in(@NonNull List<String> expectations) {
            Assert.elementsNotNull(expectations, "Expectations must not be null!");

            return new RecommendationFilter(TYPE, field, IN, expectations);
        }

        public RecommendationFilter has(@NonNull String expectation) {
            Assert.notNull(expectation, "Expectation must not be null!");

            return new RecommendationFilter(TYPE, field, HAS, expectation);
        }

        public RecommendationFilter overlaps(@NonNull List<String> expectations) {
            Assert.elementsNotNull(expectations, "Expectations must not be null!");

            return new RecommendationFilter(TYPE, field, OVERLAPS, expectations);
        }
    }

    public static class Include {

        private static final String TYPE = "INCLUDE";

        protected String field;

        public Include(String field) {
            this.field = field;
        }

        public RecommendationFilter is(@NonNull String expectation) {
            Assert.notNull(expectation, "Expectation must not be null!");

            return new RecommendationFilter(TYPE, field, IS, expectation);
        }

        public RecommendationFilter in(@NonNull List<String> expectations) {
            Assert.elementsNotNull(expectations, "Expectations must not be null!");

            return new RecommendationFilter(TYPE, field, IN, expectations);
        }

        public RecommendationFilter has(@NonNull String expectation) {
            Assert.notNull(expectation, "Expectation must not be null!");

            return new RecommendationFilter(TYPE, field, HAS, expectation);
        }

        public RecommendationFilter overlaps(@NonNull List<String> expectations) {
            Assert.elementsNotNull(expectations, "Expectations must not be null!");

            return new RecommendationFilter(TYPE, field, OVERLAPS, expectations);
        }
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RecommendationFilter that = (RecommendationFilter) o;
        return Objects.equals(type, that.type) &&
                Objects.equals(field, that.field) &&
                Objects.equals(comparison, that.comparison) &&
                Objects.equals(expectations, that.expectations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, field, comparison, expectations);
    }

    @Override
    public String toString() {
        return "RecommendationFilter{" +
                "type='" + type + '\'' +
                ", field='" + field + '\'' +
                ", comparison='" + comparison + '\'' +
                ", expectations=" + expectations +
                '}';
    }
}