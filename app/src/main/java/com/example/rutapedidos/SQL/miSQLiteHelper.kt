package com.example.rutapedidos.SQL

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class miSQLiteHelper(context: Context) :
    SQLiteOpenHelper(context, "rutapedidos.db", null, 1) {

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    override fun onCreate(db: SQLiteDatabase) {

        db.execSQL("""
            CREATE TABLE usuario (
                id_usuario INTEGER PRIMARY KEY AUTOINCREMENT,
                firebase_uid TEXT UNIQUE NOT NULL,
                rut TEXT,
                nombre TEXT,
                email TEXT NOT NULL
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE cliente (
                id_cliente INTEGER PRIMARY KEY AUTOINCREMENT,
                rut TEXT NOT NULL,
                razon_social TEXT NOT NULL,
                direccion TEXT,
                comuna TEXT,
                telefono TEXT,
                dia_visita TEXT
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE producto (
                id_producto INTEGER PRIMARY KEY AUTOINCREMENT,
                codigo TEXT NOT NULL,
                nombre TEXT NOT NULL,
                unidad_medida TEXT,
                precio INTEGER NOT NULL,
                stock INTEGER NOT NULL DEFAULT 0
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE pedido (
                id_pedido INTEGER PRIMARY KEY AUTOINCREMENT,
                id_cliente INTEGER NOT NULL,
                id_usuario INTEGER NOT NULL,
                fecha_emision TEXT NOT NULL,
                fecha_entrega TEXT,
                orden_compra TEXT,
                neto INTEGER NOT NULL DEFAULT 0,
                descuento INTEGER NOT NULL DEFAULT 0,
                impuesto INTEGER NOT NULL DEFAULT 0,
                total INTEGER NOT NULL DEFAULT 0,
                estado TEXT NOT NULL DEFAULT 'pendiente',
                sincronizado INTEGER NOT NULL DEFAULT 0,
                FOREIGN KEY (id_cliente)
                    REFERENCES cliente(id_cliente),
                FOREIGN KEY (id_usuario)
                    REFERENCES usuario(id_usuario)
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE detalle_pedido (
                id_detalle INTEGER PRIMARY KEY AUTOINCREMENT,
                id_pedido INTEGER NOT NULL,
                id_producto INTEGER NOT NULL,
                cantidad INTEGER NOT NULL,
                precio_unitario INTEGER NOT NULL,
                porcentaje_descuento REAL DEFAULT 0,
                descuento INTEGER NOT NULL DEFAULT 0,
                neto INTEGER NOT NULL DEFAULT 0,
                impuesto INTEGER NOT NULL DEFAULT 0,
                total INTEGER NOT NULL DEFAULT 0,
                FOREIGN KEY (id_pedido)
                    REFERENCES pedido(id_pedido),
                FOREIGN KEY (id_producto)
                    REFERENCES producto(id_producto)
            )
        """.trimIndent())

    }

    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {

        var usuarioBorrado = "DROP TABLE IF EXISTS usuario"
        db!!.execSQL(usuarioBorrado)
        onCreate(db)

        var clienteBorrado = "DROP TABLE IF EXISTS cliente"
        db!!.execSQL(clienteBorrado)
        onCreate(db)

        var productoBorrado = "DROP TABLE IF EXISTS producto"
        db!!.execSQL(productoBorrado)
        onCreate(db)

        var pedidoBorrado = "DROP TABLE IF EXISTS pedido"
        db!!.execSQL(pedidoBorrado)
        onCreate(db)

        var detallePedidoBorrado = "DROP TABLE IF EXISTS detalle_pedido"
        db!!.execSQL(detallePedidoBorrado)
        onCreate(db)

    }

}