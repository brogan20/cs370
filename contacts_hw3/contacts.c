#include <assert.h>
#include <limits.h>
#include <math.h>
#include <stdbool.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define ALPHABET_LENGTH    26
#define OPERATION_BUF_SIZE  7 /* Large enough to cover the word 'search' and '\0' */
#define NAME_BUF_SIZE      22

/* Basic trie node -- also, keep track of number of nodes below this one. */
typedef struct node {
    int prefix_count;
    /* Allocate +1 for the the pointer to the end-of-string marker. Needed
       for the search feature. */
    struct node *children[ALPHABET_LENGTH + 1];
} trie_node;

char* readline();
char** split_string(char*);
void init_node(trie_node*);

void init_node(trie_node *n) {
    n->prefix_count = 0;
    
    for(int i = 0; i < ALPHABET_LENGTH+1; i++) {
        n->children[i] = NULL;
    }
}
 
 
void do_add(char *word, trie_node *n) {
    while(*word != '\0') {
        if(n->children[*word - 'a'] == NULL) {
            n->children[*word - 'a'] = (trie_node*) malloc(sizeof(trie_node));
            init_node(n->children[*word - 'a']);
        }

        n = n->children[*word - 'a'];
        n->prefix_count++;
        word++;
    }

    // Add end-of-string marker
    if(n->children[ALPHABET_LENGTH] == NULL) {
            n->children[ALPHABET_LENGTH] = (trie_node*) malloc(sizeof(trie_node));
            init_node(n->children[ALPHABET_LENGTH]);
        }

    n = n->children[ALPHABET_LENGTH];
    n->prefix_count++;
}

int do_find(char *word, trie_node *n) {
    while(*word != '\0') {
        // Not in list
        if(n->children[*word - 'a'] == NULL) {
            return 0;
        }

        n = n->children[*word - 'a'];
        word++;
    }

    return n->prefix_count;
}

/*
 * Complete the contacts function below.
 */

/*
 * Please store the size of the integer array to be returned in result_count pointer. For example,
 * int a[3] = {1, 2, 3};
 *
 * *result_count = 3;
 *
 * return a;
 *
 */
 
int* contacts(int queries_rows, int queries_columns, char*** queries, int* result_count) {
    /*
     * Write your code here.
     */
    
    trie_node root;
    init_node(&root);

    int *a = (int*)malloc(queries_rows * sizeof(int));
    int find_count = 0;
    
    for(int i = 0; i < queries_rows; i++) {
        char **query = queries[i];
        
        char *op = query[0];
        char *word = query[1];
        
        if(strcmp(op, "add") == 0) {
            do_add(word, &root);
        }
        else if(strcmp(op, "find") == 0) {
            a[find_count++] = do_find(word, &root);
            //find_count++;
        }
        else {
            fprintf(stderr, "Input wrong type\n");
        }           
    }

    *result_count = find_count;
    return a;
}

int main()
{
    FILE* fptr = fopen(getenv("OUTPUT_PATH"), "w");

    char* queries_rows_endptr;
    char* queries_rows_str = readline();
    int queries_rows = strtol(queries_rows_str, &queries_rows_endptr, 10);

    if (queries_rows_endptr == queries_rows_str || *queries_rows_endptr != '\0') { exit(EXIT_FAILURE); }

    char*** queries = malloc(queries_rows * sizeof(char**));

    for (int queries_row_itr = 0; queries_row_itr < queries_rows; queries_row_itr++) {
        queries[queries_row_itr] = malloc(2 * (sizeof(char*)));

        char** queries_item_temp = split_string(readline());

        for (int queries_column_itr = 0; queries_column_itr < 2; queries_column_itr++) {
            char* queries_item = queries_item_temp[queries_column_itr];

            queries[queries_row_itr][queries_column_itr] = queries_item;
        }
    }

    int result_count;
    int* result = contacts(queries_rows, 2, queries, &result_count);

    for (int result_itr = 0; result_itr < result_count; result_itr++) {
        fprintf(fptr, "%d", result[result_itr]);

        if (result_itr != result_count - 1) {
            fprintf(fptr, "\n");
        }
    }

    fprintf(fptr, "\n");

    fclose(fptr);

    return 0;
}

char* readline() {
    size_t alloc_length = 1024;
    size_t data_length = 0;
    char* data = malloc(alloc_length);

    while (true) {
        char* cursor = data + data_length;
        char* line = fgets(cursor, alloc_length - data_length, stdin);

        if (!line) { break; }

        data_length += strlen(cursor);

        if (data_length < alloc_length - 1 || data[data_length - 1] == '\n') { break; }

        size_t new_length = alloc_length << 1;
        data = realloc(data, new_length);

        if (!data) { break; }

        alloc_length = new_length;
    }

    if (data[data_length - 1] == '\n') {
        data[data_length - 1] = '\0';
    }

    data = realloc(data, data_length);

    return data;
}

char** split_string(char* str) {
    char** splits = NULL;
    char* token = strtok(str, " ");

    int spaces = 0;

    while (token) {
        splits = realloc(splits, sizeof(char*) * ++spaces);
        if (!splits) {
            return splits;
        }

        splits[spaces - 1] = token;

        token = strtok(NULL, " ");
    }

    return splits;
}
