import React, {useEffect, useState} from "react";
import {Table} from "react-bootstrap";
import {
    getCoreRowModel,
    getSortedRowModel,
    useReactTable
} from "@tanstack/react-table";
import Spinner from "../Spinner";
import {
    closestCenter,
    DndContext,
    KeyboardSensor,
    MouseSensor,
    TouchSensor,
    useSensor,
    useSensors
} from "@dnd-kit/core";
import {restrictToHorizontalAxis} from "@dnd-kit/modifiers";
import FlexibleTableHeader from "./FlexibleTableHeader";
import FlexibleTableCell from "./FlexibleTableCell";
import {
    arrayMove,
    horizontalListSortingStrategy,
    SortableContext
} from "@dnd-kit/sortable";

//initColumns는 테이블 헤더를 정의한 값이다.포멧에 맞게 정의해야한다.
//schedule.js를 참고하기 바람
//data는 table body에 렌더링될 row단위 배열이다.
//isLoading은 스피너를 내장화 한 것임
const FlexibleTable = ({ initColumns, data, isLoading, defaultSorting }) => {
    const [openColumn, setOpenColumn] = useState(null); //sort, align메뉴 펼침 상태 여부
    const [sorting, setSorting] = useState([]); // 컬럼 정렬 상태
    const [columns, setColumns] = useState(initColumns.map((column) => {
        const alignColumn = {...column, meta: { align: 'start' }, id: column.accessorKey};
        return alignColumn;
    }));

    const [columnOrder, setColumnOrder] = useState(() => columns.map((c) => c.id));

    // 초기 정렬
    useEffect(() => {
        if (defaultSorting) {
            setSorting([defaultSorting]);
        }
    }, [defaultSorting]);

    // 숫자인 경우 숫자, 문자인 경우 문자로 정렬 (리엑트 테이블의 기본 정렬기능은 문자정렬만 지원 하기에 숫자 정렬기능 추가)
    const dynamicSort = (a, b, columnId) => {
        const aValue = a.getValue(columnId);
        const bValue = b.getValue(columnId);

        if (aValue === null || aValue === undefined || aValue === "") return 1;
        if (bValue === null || bValue === undefined || bValue === "") return -1;

        // 숫자인 경우 숫자형으로 변환
        const aNum = parseFloat(aValue);
        const bNum = parseFloat(bValue);

        const isANumber = !isNaN(aNum);
        const isBNumber = !isNaN(bNum);

        if (isANumber && isBNumber) {
            // 숫자 정렬
            return aNum - bNum;
        } else {
            // 문자열 정렬 (대소문자 구분 없이)
            return aValue.toString().localeCompare(bValue.toString(), undefined, { sensitivity: 'base' });
        }
    };

    // 보안위험 정렬
    const sortSecurityRisk = (a, b, columnId) => {
        const priorityKeys = ["securityCritical", "securityHigh", "securityMedium", "securityLow", "securityOk"];

        for (const key of priorityKeys) {
            const aValue = a.original[key] || 0;
            const bValue = b.original[key] || 0;

            const diff = aValue -  bValue;
            if (diff !== 0) return diff;
        }

        return 0;
    };

    // 라이선스위험 정렬
    const sortLicenseRisk = (a, b, columnId) => {
        const priorityKeys = ["licenseHigh", "licenseMedium", "licenseLow", "licenseOk"];

        for (const key of priorityKeys) {
            const aValue = a.original[key] || 0;
            const bValue = b.original[key] || 0;

            const diff = aValue - bValue;
            if (diff !== 0) return diff;
        }

        return 0;
    };

    const table = useReactTable({
        data: data || [],
        columns: columns || [],
        getCoreRowModel: getCoreRowModel(), // 기본 행 모델
        getSortedRowModel: getSortedRowModel(), // 정렬 가능하게 설정
        state: { sorting, columnOrder },
        onSortingChange: setSorting,
        onColumnOrderChange: setColumnOrder,
        enableColumnResizing: true,
        columnResizeMode: 'onChange',
        sortingFns: {
            dynamicSort: dynamicSort,
            sortSecurityRisk: sortSecurityRisk,
            sortLicenseRisk: sortLicenseRisk
        }
        // debugTable: true,
        // debugHeaders: true,
        // debugColumns: true,
    });

    const sensors = useSensors(
        useSensor(MouseSensor, {
            activationConstraint: {
                distance: 8,
            },
        }),
        useSensor(TouchSensor, {}),
        useSensor(KeyboardSensor, {})
    )

    //정렬 메뉴 클릭 이벤트
    const toggleMenu = (column) => {
        setOpenColumn(openColumn === column ? null : column);
    };

    //정렬 이벤트
    const handleSort = (id, desc) => {
        setSorting([{ id, desc }])
        setOpenColumn(null); // 정렬 후 메뉴 닫기
    };

    //align 이벤트
    const handleAlign = (id, align) => {
        const alignColumns = columns.map((column) => {
            if (column.accessorKey === id) {
                return {...column, meta: {align: align}};
            } else {
                return column
            }
        });
        setColumns(alignColumns);
        setOpenColumn(null); // 정렬 후 메뉴 닫기
    }

    // reorder columns after drag & drop
    function handleDragEnd(event) {
        const { active, over } = event
        if (active && over && active.id !== over.id) {
            setColumnOrder(columnOrder => {
                const oldIndex = columnOrder.indexOf(active.id);
                const newIndex = columnOrder.indexOf(over.id);
                return arrayMove(columnOrder, oldIndex, newIndex); //this is just a splice util
            })
        }
    }

    // initColumns의 값이 바뀔때마다 columns와 columnOrder 세팅
    useEffect(() => {
        const newColumns = initColumns.map((column) => {
            return { ...column, meta: { align: 'start' }, id: column.accessorKey };
        });

        setColumns(newColumns);
        setColumnOrder(newColumns.map((c) => c.id));
    }, [initColumns]);

    return (
        <DndContext
            collisionDetection={closestCenter}
            modifiers={[restrictToHorizontalAxis]}
            onDragEnd={handleDragEnd}
            sensors={sensors}
        >
            <Table style={{ wordBreak: "break-all" /* 글자 단위 줄바꿈 */}}>
                <thead>
                    {table.getHeaderGroups().map((headerGroup) => {
                        return <tr key={headerGroup.id}>
                            <SortableContext items={columnOrder}
                                             strategy={horizontalListSortingStrategy}
                            >
                                {headerGroup.headers.map((header) => {
                                    return <FlexibleTableHeader key={header.id} header={header}
                                                                openColumn={openColumn}
                                                                toggleMenu={toggleMenu}
                                                                handleSort={handleSort}
                                                                handleAlign={handleAlign}/>
                                })}
                            </SortableContext>
                        </tr>
                    })}
                </thead>
                <tbody>
                    {isLoading ? <tr>
                        <td colSpan={columns.length} className="text-center">
                            <Spinner isTable={true} />
                        </td>
                    </tr> :
                        table.getRowModel().rows.length === 0 ? <tr>
                            <td colSpan={columns.length} className="text-center">
                                데이터가 존재하지 않습니다
                            </td>
                        </tr> :
                        table.getRowModel().rows.map((row) => {
                        return <tr key={row.id}>
                            {row.getVisibleCells().map((cell) => {
                                return <SortableContext
                                    key={cell.id}
                                    items={columnOrder}
                                    strategy={horizontalListSortingStrategy}
                                >
                                    <FlexibleTableCell key={cell.id} cell={cell} />
                                </SortableContext>
                            })}
                        </tr>
                    })}
                </tbody>
            </Table>
        </DndContext>
    );
}

export default FlexibleTable;