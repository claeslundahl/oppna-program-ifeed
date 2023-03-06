package se.vgregion.ifeed.types;

@Deprecated // Is this used?
public class Field {

        public Field() {
            super();
        }

        private String name;

        public String getName() {
            return this.name;
        }

        public void setName(String name) {
            this.name = name;
        }

        private String type;

        public String getType() {
            return this.type;
        }

        public void setType(String type) {
            this.type = type;
        }

        private boolean multiValued;

        public boolean getMultiValued() {
            return this.multiValued;
        }

        public void setMultiValued(boolean multiValued) {
            this.multiValued = multiValued;
        }

        private boolean indexed;

        public boolean getIndexed() {
            return this.indexed;
        }

        public void setIndexed(boolean indexed) {
            this.indexed = indexed;
        }

        private boolean required;

        public boolean getRequired() {
            return this.required;
        }

        public void setRequired(boolean required) {
            this.required = required;
        }

        private boolean stored;

        public boolean getStored() {
            return this.stored;
        }

        public void setStored(boolean stored) {
            this.stored = stored;
        }

        private String _default;

        public String getDefault() {
            return this._default;
        }

        public void setDefault(String _default) {
            this._default = _default;
        }

        private Boolean omitNorms;

        public Boolean getOmitNorms() {
            return this.omitNorms;
        }

        public void setOmitNorms(Boolean omitNorms) {
            this.omitNorms = omitNorms;
        }


        @Override
        public String toString() {
            return "Field{" +
                    "name='" + name + '\'' +
                    ", type='" + type + '\'' +
                    ", multiValued=" + multiValued +
                    ", indexed=" + indexed +
                    ", required=" + required +
                    ", stored=" + stored +
                    ", _default='" + _default + '\'' +
                    ", omitNorms=" + omitNorms +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Field)) return false;

            Field field = (Field) o;

            if (multiValued != field.multiValued) return false;
            if (indexed != field.indexed) return false;
            if (required != field.required) return false;
            if (stored != field.stored) return false;
            if (name != null ? !name.equals(field.name) : field.name != null) return false;
            if (type != null ? !type.equals(field.type) : field.type != null) return false;
            if (_default != null ? !_default.equals(field._default) : field._default != null) return false;
            return omitNorms != null ? omitNorms.equals(field.omitNorms) : field.omitNorms == null;
        }

        @Override
        public int hashCode() {
            int result = name != null ? name.hashCode() : 0;
            result = 31 * result + (type != null ? type.hashCode() : 0);
            result = 31 * result + (multiValued ? 1 : 0);
            result = 31 * result + (indexed ? 1 : 0);
            result = 31 * result + (required ? 1 : 0);
            result = 31 * result + (stored ? 1 : 0);
            result = 31 * result + (_default != null ? _default.hashCode() : 0);
            result = 31 * result + (omitNorms != null ? omitNorms.hashCode() : 0);
            return result;
        }
    }