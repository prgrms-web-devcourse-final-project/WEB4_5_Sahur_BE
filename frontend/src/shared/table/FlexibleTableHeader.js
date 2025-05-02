import {flexRender} from "@tanstack/react-table";
import clsx from "clsx";
import style from "./FlexibleTable.module.scss";
import React, {useEffect} from "react";
import {useSortable} from "@dnd-kit/sortable";
import {CSS} from '@dnd-kit/utilities';
import {Button} from "react-bootstrap";

const FlexibleTableHeader = ({ header, openColumn, handleAlign, handleSort, toggleMenu }) => {
    const { attributes, isDragging, listeners, setNodeRef, transform } = useSortable({id: header.column.id,})
    const thStyle = {
        opacity: isDragging ? 0.8 : 1,
        position: 'relative',
        transform: CSS.Translate.toString(transform), // translate instead of transform to avoid squishing
        transition: 'width transform 0.2s ease-in-out',
        whiteSpace: 'nowrap',
        width: header.column.getSize(),
        zIndex: isDragging ? 1 : 0,
    }

    //컬럼메뉴 외부 클릭시 컬럼메뉴를 닫는 함수
    const handleOutsideClick = (e) => {
        if (e.target.tagName !== 'BUTTON' && openColumn) {
            toggleMenu(openColumn);
        }
    }

    // 컬럼메뉴가 열려 있는 상태에서 컬럼메뉴 외부를 클릭하면 컬럼메뉴가 닫히기 위한 side effect
    useEffect(() => {
        document.addEventListener('click', handleOutsideClick);
        return () => {
            document.removeEventListener('click', handleOutsideClick);
        };
    }, []);

    return (
        <th key={header.id} colSpan={header.colSpan}
            ref={setNodeRef} style={thStyle}
            /*style={{ position: 'relative' }}*/>
            <div className="kw-list-table-title cursor-move" {...attributes} {...listeners}>
                {flexRender(header.column.columnDef.header, header.getContext())}
                {header.column.columnDef.sortable && // sortable이 false면 sorting버튼 렌더링 안함
                    <div className={clsx("kw-align", {
                        "kw-align--open": openColumn === header.id
                    })}
                    >
                        <Button bsPrefix={`kw-align-${header.column.columnDef.meta.align}`} onClick={() => toggleMenu(header.id)}>
                            정렬
                        </Button>
                        <div className="kw-align-layer">
                            <Button bsPrefix={"kw-align-start"} onClick={() => handleAlign(header.id, "start")}>왼쪽</Button>
                            <Button bsPrefix={"kw-align-center"} onClick={() => handleAlign(header.id, "center")}>가운데</Button>
                            <Button bsPrefix={"kw-align-end"} onClick={() => handleAlign(header.id, "end")}>오른쪽</Button>
                            <hr/>
                            <Button bsPrefix={"kw-align-asc"} onClick={() => handleSort(header.id, false)}>오름차순</Button>
                            <Button bsPrefix={"kw-align-desc"} onClick={() => handleSort(header.id, true)}>내림차순</Button>
                        </div>
                    </div>
                }
            </div>
            {header.column.getCanResize() && (
                <div
                    onMouseDown={header.getResizeHandler()}
                    onTouchStart={header.getResizeHandler()}
                    className={`${style.resizer} ${
                        header.column.getIsResizing() ? style.isResizing : ''
                    }`}
                ></div>
            )}
        </th>
    );
}

export default FlexibleTableHeader;