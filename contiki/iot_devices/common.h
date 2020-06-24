#ifndef COMMON_H
#define COMMON_H

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

static int coap_get_variable_json(const char *buffer, size_t length,
                                  const char *name, const char **output) {
    const char *start = NULL;
    const char *end = NULL;
    const char *value_end = NULL;
    size_t name_len = 0;

    /*initialize the output buffer first */
    *output = 0;

    name_len = strlen(name);
    end = buffer + length;

    for (start = buffer; start + name_len < end; ++start) {
        if ((start == buffer || start[-1] == ',' || start[-1] == '{') &&
            start[name_len] == ':' && strncmp(name, start, name_len) == 0) {

            /* Point start to variable value */
            start += name_len + 1;

            /* Point end to the end of the value */
            value_end = (const char *)memchr(start, ',', end - start);
            if (value_end == NULL) {
                value_end = end - 1;
            }
            *output = start;

            return value_end - start;
        }
    }
    return 0;
}

#endif /* COMMON_H */