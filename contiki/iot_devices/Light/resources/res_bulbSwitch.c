#include "contiki.h"

#include "coap-engine.h"
#include <limits.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "sys/log.h"
#include "os/dev/leds.h"

#define LOG_MODULE "BulbSwitch actuator"
#define LOG_LEVEL LOG_LEVEL_DBG

static void res_get_handler(coap_message_t *request, coap_message_t *response,
                            uint8_t *buffer,uint16_t preferred_size,int32_t *offset);

static void res_post_put_handler(coap_message_t *request,coap_message_t *response,
				uint8_t *buffer,uint16_t preferred_size, int32_t *offset);


static int bulbSwitch_mode = 0;

RESOURCE(res_bulbSwitch,
         "title=\"BulbSwitch actuator\";methods=\"GET/PUT/POST\", mode=OFF|LOW|MEDIUM|HIGH\";rt=\"int\"\n",
         res_get_handler,
	 res_post_put_handler,
	 res_post_put_handler,
	 NULL);


static void res_get_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size,int32_t *offset) {

    unsigned int accept = -1;
    if (!coap_get_header_accept(request, &accept))
        accept = APPLICATION_JSON;

    if (accept == APPLICATION_JSON) {
		char *res_mode = NULL;

		switch(bulbSwitch_mode){
			case 0:
				res_mode = "OFF";
				break;
			case 1:
				res_mode = "LOW";
				break;
			case 2:
				res_mode = "MEDIUM";
				break;
			case 3:
				res_mode = "HIGH";
				break;
		}

		snprintf((char *)buffer, COAP_MAX_CHUNK_SIZE, "{\"mode\":\"%s\"}",res_mode);
		coap_set_header_content_format(response, APPLICATION_JSON);
		coap_set_payload(response, buffer, strlen((char *)buffer));

    } else {
        coap_set_status_code(response, NOT_ACCEPTABLE_4_06);
        const char *msg = "Supporting content-type application/json";
        coap_set_payload(response, msg, strlen(msg));
    }
}


static void res_post_put_handler(coap_message_t *request,coap_message_t *response, uint8_t *buffer,uint16_t preferred_size, int32_t *offset) {
    size_t len = 0;
    const char *value = NULL;
    int success = 1;

    if ((len = coap_get_post_variable(request,"\"mode\"",&value))) {
        if (strncmp(value, "\"OFF\"", len) == 0) {
            bulbSwitch_mode = 0;
			leds_single_off(LEDS_GREEN);
			leds_single_off(LEDS_RED);
			leds_single_off(LEDS_YELLOW);

        } else if (strncmp(value, "\"LOW\"", len) == 0) {
            bulbSwitch_mode = 1;
			leds_single_off(LEDS_GREEN);
			leds_single_off(LEDS_RED);
			leds_single_off(LEDS_YELLOW);
			leds_single_on(LEDS_GREEN);

        } else if (strncmp(value, "\"MEDIUM\"", len) == 0) {
            bulbSwitch_mode = 2;
			leds_single_off(LEDS_GREEN);
			leds_single_off(LEDS_RED);
			leds_single_off(LEDS_YELLOW);
			leds_single_on(LEDS_YELLOW);

        }else if (strncmp(value, "\"HIGH\"", len) == 0) {
            bulbSwitch_mode = 3;
			leds_single_off(LEDS_GREEN);
			leds_single_off(LEDS_RED);
			leds_single_off(LEDS_YELLOW);
			leds_single_on(LEDS_RED);

        }else
            success = 0;
    } else
        success = 0;
    
    if (!success) 
        coap_set_status_code(response, BAD_REQUEST_4_00);
    else
    	coap_set_status_code(response, CHANGED_2_04);
}

