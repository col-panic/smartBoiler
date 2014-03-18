//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.03.18 at 08:32:22 AM CET 
//


package com.bosch.vpp;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Method.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="Method">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="create"/>
 *     &lt;enumeration value="delete"/>
 *     &lt;enumeration value="execute"/>
 *     &lt;enumeration value="get"/>
 *     &lt;enumeration value="query"/>
 *     &lt;enumeration value="update"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "Method")
@XmlEnum
public enum Method {

    @XmlEnumValue("create")
    CREATE("create"),
    @XmlEnumValue("delete")
    DELETE("delete"),
    @XmlEnumValue("execute")
    EXECUTE("execute"),
    @XmlEnumValue("get")
    GET("get"),
    @XmlEnumValue("query")
    QUERY("query"),
    @XmlEnumValue("update")
    UPDATE("update");
    private final String value;

    Method(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static Method fromValue(String v) {
        for (Method c: Method.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}