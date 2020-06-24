#include "contiki.h"

#include "coap-engine.h"
#include <limits.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include "random.h"

/* Log configuration */
#include "sys/log.h"

#define LOG_MODULE "Light sensor"
#define LOG_LEVEL LOG_LEVEL_DBG
#define MAX_AGE 60

static int light_intensity = 5;

/* function to generate realistic value as soon as possible */
static int read_light_sensor(){
	int max = 10;
	int min = 0;
	int new_value = light_intensity + ((random_rand() % 3)-1);
	light_intensity = (new_value > max) ? max : (new_value < min)? min : new_value;
	return light_intensity;
}


static void res_get_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size,int32_t *offset);

EVENT_RESOURCE(res_light,"title=\"Light sensor\";methods=\"GET\";rt=\"int\"\n", res_get_handler, NULL, NULL, NULL, NULL);


static void res_get_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset) {

    unsigned int accept = -1;
    if (!coap_get_header_accept(request, &accept))
        accept = APPLICATION_JSON;

    if (accept == APPLICATION_JSON) {
	int val = read_light_sensor();
        coap_set_header_content_format(response, APPLICATION_JSON);
        snprintf((char *)buffer, COAP_MAX_CHUNK_SIZE, "{\"light\":%d}", val);

        coap_set_payload(response, buffer, strlen((char *)buffer));
    } else {
        coap_set_status_code(response, NOT_ACCEPTABLE_4_06);
        const char *msg = "Supporting content-type application/json";
        coap_set_payload(response, msg, strlen(msg));
    }

    coap_set_header_max_age(response, MAX_AGE);

    /* The coap_subscription_handler() will be called for observable resources
     * by the coap_framework. */
}

