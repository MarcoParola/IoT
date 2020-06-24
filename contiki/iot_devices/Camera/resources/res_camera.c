#include "contiki.h"

#include "coap-engine.h"
#include <limits.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "time.h"
#include "random.h"
#include "sys/log.h"

#define LOG_MODULE "Camera sensor"
#define LOG_LEVEL LOG_LEVEL_DBG
#define MAX_AGE 60

#define PERIOD (3 * CLOCK_SECOND)

static int counter = 0;

static void res_get_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size,int32_t *offset);
static void res_event_handler();


PERIODIC_RESOURCE(res_camera,"title=\"Camera sensor\";obs;rt=\"Camera sensor\"\n",
		res_get_handler,
		NULL, 
		NULL, 
		NULL, 
		PERIOD,
		res_event_handler);


static void res_get_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset) {

    unsigned int accept = -1;
    if (!coap_get_header_accept(request, &accept))
        accept = APPLICATION_JSON;

    if (accept == APPLICATION_JSON) {

	int val = counter;
	unsigned long unix_timestamp = (unsigned long)time(NULL);

        coap_set_header_content_format(response, APPLICATION_JSON);
        snprintf((char *)buffer, COAP_MAX_CHUNK_SIZE, "{\"camera\":%d, \"timestamp\":%lu}", val, unix_timestamp);
        coap_set_payload(response, buffer, strlen((char *)buffer));

    } else {
        coap_set_status_code(response, NOT_ACCEPTABLE_4_06);
        const char *msg = "Supporting content-type application/json";
        coap_set_payload(response, msg, strlen(msg));
    }

    coap_set_header_max_age(response, MAX_AGE);
}



static void res_event_handler(){
	counter++;
	LOG_DBG("obs sending %d\n", counter);
	coap_notify_observers(&res_camera);
}
