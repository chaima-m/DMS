{
    'name': 'ENSIA DMS',
    'version': '15.0.2.0.0',
    'category': 'Document Management',
    'summary': 'Document Management System for ENSIA Enterprise Computing',
    'description': '''
        Full DMS module with:
        - Documents with categories and departments
        - Role-based access control (Manager / User)
        - Attachment support
        - Kanban, List, Form, and Search views
    ''',
    'author': 'ENSIA',
    'depends': ['base', 'mail'],
    'data': [
        'security/groups.xml',
        'security/ir.model.access.csv',
        'views/document_views.xml',
        'views/menu.xml',
    ],
    'installable': True,
    'application': True,
    'auto_install': False,
}
