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
 * <p>Java class for Context.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="Context">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="action"/>
 *     &lt;enumeration value="measurement"/>
 *     &lt;enumeration value="ping"/>
 *     &lt;enumeration value="state"/>
 *     &lt;enumeration value="timeSeries"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "Context")
@XmlEnum
public enum Context {

    @XmlEnumValue("action")
    ACTION("action"),
    @XmlEnumValue("measurement")
    MEASUREMENT("measurement"),
    @XmlEnumValue("ping")
    PING("ping"),
    @XmlEnumValue("state")
    STATE("state"),
    @XmlEnumValue("timeSeries")
    TIME_SERIES("timeSeries");
    private final String value;

    Context(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static Context fromValue(String v) {
        for (Context c: Context.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
