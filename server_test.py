"""
Acceptance tests for the tagging/tokenizing server.

These just test the shape of the response, not the
actual content coming out of the tagger/tokenizer.

The assumption is the server is started separately 
before these tests are run.
"""
import requests

SERVER_URL = "http://localhost:8080"


# TODO the server is only handling string keys in the POST for now
# perhaps it should handle integers as keys in the POST properly


def test_empty():
    response = requests.post(SERVER_URL)
    assert response.json() == {}


def test_just_tokenize():
    data = {'123': 'whose woods these are I think I know',
            '456': 'lol wut 420 yolo'}
    response = requests.post(SERVER_URL + "/tokenize", data=data)
    assert response.status_code == 200

    response_obj = response.json()

    assert '123' in response_obj
    assert '456' in response_obj

    assert response_obj['123']['original'] == data['123']
    assert 'tokens' in response_obj['123']
    assert 'tags' not in response_obj['123']

    assert response_obj['456']['original'] == data['456']
    assert 'tokens' in response_obj['456']
    assert 'tags' not in response_obj['456']


def test_tokenize_and_tag():
    data = {'123': 'whose woods these are I think I know',
            '456': 'lol wut 420 yolo'}
    response = requests.post(SERVER_URL + "/tokenize_and_tag", data=data)
    assert response.status_code == 200

    response_obj = response.json()

    assert '123' in response_obj
    assert '456' in response_obj

    assert response_obj['123']['original'] == data['123']
    assert 'tokens' in response_obj['123']
    assert 'tags' in response_obj['123']
    assert len(response_obj['123']['tags']) == len(response_obj['123']['tokens'])

    assert response_obj['456']['original'] == data['456']
    assert 'tokens' in response_obj['456']
    assert 'tags' in response_obj['456']
    assert len(response_obj['456']['tags']) == len(response_obj['456']['tokens'])
    
