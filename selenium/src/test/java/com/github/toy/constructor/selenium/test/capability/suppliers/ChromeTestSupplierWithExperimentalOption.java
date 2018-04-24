package com.github.toy.constructor.selenium.test.capability.suppliers;

import com.github.toy.constructor.selenium.properties.AdditionalCapabilitiesFor;
import com.github.toy.constructor.selenium.properties.CapabilitySupplier;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.HashMap;

import static com.github.toy.constructor.selenium.properties.CapabilityTypes.CHROME;

@AdditionalCapabilitiesFor(type = CHROME, supplierName = "withExperimental")
public class ChromeTestSupplierWithExperimentalOption implements CapabilitySupplier {
    @Override
    public Capabilities get() {
        return new ChromeOptions().setExperimentalOption("experimentalOption", new HashMap<>())
                .addArguments("--use-fake-device-for-media-stream",
                "--start-maximized", "--enable-automation",
                "--disable-web-security");
    }
}
