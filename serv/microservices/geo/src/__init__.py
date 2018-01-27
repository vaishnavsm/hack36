from flask import Flask
import os

DB_USERNAME = os.environ['POSTGRES_USERNAME']
DB_PASSWORD = os.environ['POSTGRES_PASSWORD']
DB_HOST = os.environ['POSTGRES_HOSTNAME']os.environ['POSTGRES_HOSTNAME']
DB_PORT = os.environ['POSTGRES_PORT']
DB_DB = "hasuradb"

app = Flask(__name__)
app.config['DEBUG'] = True
app.config['DEVELOPMENT'] = True
app.config['TESTING'] = True
app.config['CSRF_ENABLED'] = False
app.config['SQLALCHEMY_DATABASE_URI'] = "postgresql://%s:%s@%s:%s/%s"%(DB_USERNAME, DB_PASSWORD, DB_HOST, DB_PORT, DB_DB)
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False

# This line adds the hasura example routes form the hasura.py file.
# Delete these two lines, and delete the file to remove them from your project
from .hasura import hasura_examples
app.register_blueprint(hasura_examples)

from .server import *
