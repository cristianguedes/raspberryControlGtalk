package com.raspberry.demos;


import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

public class ControlGpioExample {
 
 public static void main(String[] args) throws InterruptedException {
     
     System.out.println("<--Pi4J--> controle GPIO iniciado.");
     
     // criando um gpio controle
     final GpioController gpio = GpioFactory.getInstance();
     
     // provision gpio pin #01 as an output pin and turn on
     final GpioPinDigitalOutput pin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_00, "MeuLED", PinState.HIGH);
     System.out.println("--> GPIO ligado");
     
     Thread.sleep(5000);
     
     // desligar gpio pino #00
     pin.low();
     System.out.println("--> GPIO desligado");

     Thread.sleep(5000);

     // alternar o estado atual do gpio pino #00 (é para ligar)
     pin.toggle();
     System.out.println("--> GPIO ligado");

     Thread.sleep(5000);

     // alternar o estado atual do gpio pino #00  (é para desligar)
     pin.toggle();
     System.out.println("--> GPIO desligado");
     
     Thread.sleep(5000);

     // turn on gpio pin #01 for 1 second and then off
     System.out.println("--> GPIO state should be: ON for only 1 second");
     pin.pulse(1000, true); // set second argument to 'true' use a blocking call
     
     // stop all GPIO activity/threads by shutting down the GPIO controller
     // (this method will forcefully shutdown all GPIO monitoring threads and scheduled tasks)
     gpio.shutdown();
 }
}
//END SNIPPET: control-gpio-snippet