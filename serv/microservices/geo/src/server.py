from src import app
from flask.ext.sqlalchemy import SQLAlchemy
# from flask import jsonify

db = SQLAlchemy(app)

@app.route("/")
def home():
    return "Hasura Hello World<br>DB: %s"%db

# Uncomment to add a new URL at /new

# @app.route("/json")
# def json_message():
#     return jsonify(message="Hello World")
